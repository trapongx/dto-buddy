package com.runninglane.dto.buddy.bytecode.k2jvm

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.io.StringWriter
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.DiagnosticCollector
import javax.tools.JavaCompiler
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject
import javax.tools.ToolProvider
import kotlin.reflect.KClass

class CompilationSession(
    val inMemory: Boolean = System.getProperty("dto-buddy.compilation.in-memory")?.toBoolean() ?: true
) {
    // Keep a global shared directory for all compilations in this session
    private val sessionDir by lazy {
        val dir = Files.createTempDirectory("compilation-session").toFile()
        dir.deleteOnExit() // Clean up on JVM shutdown
        dir
    }

    // Keep track of all source files created in this session
    private val createdKotlinSources = mutableMapOf<String, File>()
    private val createdJavaSources = mutableMapOf<String, File>()

    // Get Java compiler once and reuse
    private val javaCompiler: JavaCompiler by lazy {
        ToolProvider.getSystemJavaCompiler() ?: throw RuntimeException("Java compiler not available. Make sure you're running with JDK, not JRE.")
    }

    /**
     * Compiles and loads a source code string.
     * Uses a persistent session directory to ensure all classes
     * remain available throughout the session.
     * 
     * @param src The source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @param language Source language, either "kotlin" or "java"
     * @return The loaded class
     */
    fun compileAndLoad(src: String, className: String, packageName: String, language: String = "kotlin"): KClass<*> {
        return when (language.lowercase()) {
            "kotlin" -> when (inMemory) {
                true -> compileAndLoadKotlinInMemory(src, className, packageName)
                false -> compileAndLoadKotlin(src, className, packageName)
            }
            "java" -> when (inMemory) {
                true -> compileAndLoadJavaInMemory(src, className, packageName)
                false -> compileAndLoadJava(src, className, packageName)
            }
            else -> throw IllegalArgumentException("Unsupported language: $language. Only 'kotlin' and 'java' are supported.")
        }
    }

    /**
     * Compiles and loads a Kotlin source string.
     * 
     * @param src The Kotlin source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @return The loaded class
     */
    private fun compileAndLoadKotlin(src: String, className: String, packageName: String): KClass<*> {
        // Get package name - either from parameter or by extracting from source
        val fullClassName = "$packageName.$className"

        // Setup directories
        val sourceDir = File(sessionDir, "src").apply { mkdirs() }
        val outputDir = File(sessionDir, "out").apply { mkdirs() }

        // Create source file
        val packageDir = File(sourceDir, fullClassName.replace('.', File.separatorChar)).apply { mkdirs() }
        val sourceFile = File(packageDir, "$className.kt")
        sourceFile.writeText(src)
        createdKotlinSources[fullClassName] = sourceFile

        // Compile using K2JVMCompiler with all source files
        val compiler = K2JVMCompiler()
        val arguments = K2JVMCompilerArguments().apply {
            freeArgs = createdKotlinSources.values.map { it.absolutePath }
            destination = outputDir.absolutePath
            noStdlib = true
            noReflect = true
            classpath = System.getProperty("java.class.path")
            jvmTarget = System.getProperty("java.version").substringBefore(".")
        }

        val messageCollector = PrintingMessageCollector(
            System.err,
            MessageRenderer.PLAIN_FULL_PATHS,
            false
        )

        val exitCode = compiler.exec(messageCollector, Services.EMPTY, arguments)
        if (exitCode.code != 0) {
            // Get the source file for better error reporting
            val sourceContent = sourceFile.readText()
            throw RuntimeException(
                "Kotlin compilation failed with exit code $exitCode.\n" +
                "Source file: ${sourceFile.absolutePath}\n" +
                "Content: \n${sourceContent.lines().take(20).joinToString("\n")}"
            )
        }

        // Load the class using the standard classloader
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return classLoader.loadClass(fullClassName).kotlin
    }

    /**
     * Compiles and loads a Java source string.
     * 
     * @param src The Java source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @return The loaded class
     */
    private fun compileAndLoadJava(src: String, className: String, packageName: String): KClass<*> {
        val fullClassName = "$packageName.$className"

        // Setup directories
        val sourceDir = File(sessionDir, "src-java").apply { mkdirs() }
        val outputDir = File(sessionDir, "out-java").apply { mkdirs() }

        // Create source file
        val packagePath = packageName.replace('.', File.separatorChar)
        val packageDir = File(sourceDir, packagePath).apply { mkdirs() }
        val sourceFile = File(packageDir, "$className.java")
        sourceFile.writeText(src)
        createdJavaSources[fullClassName] = sourceFile

        // Create a diagnostic collector to capture compilation errors
        val diagnostics = DiagnosticCollector<JavaFileObject>()

        // Get the file manager from the compiler
        val fileManager = javaCompiler.getStandardFileManager(diagnostics, null, null)

        // Create compilation units from the source files
        val compilationUnits = fileManager.getJavaFileObjects(*createdJavaSources.values.toTypedArray())

        // Prepare compilation task with the classpath
        val options = listOf(
            "-d", outputDir.absolutePath,
            "-classpath", System.getProperty("java.class.path")
        )

        // Create a writer for compiler output
        val writer = StringWriter()

        // Execute the compiler task
        val task = javaCompiler.getTask(
            writer,
            fileManager,
            diagnostics,
            options,
            null,
            compilationUnits
        )

        val success = task.call()
        if (!success) {
            val errorMessages = diagnostics.diagnostics.joinToString("\n") { diagnostic ->
                "${diagnostic.source?.name ?: "Unknown source"}: " +
                "line ${diagnostic.lineNumber}, position ${diagnostic.columnNumber}: " +
                diagnostic.getMessage(null)
            }

            val sourceContent = sourceFile.readText()
            throw RuntimeException(
                "Java compilation failed:\n$errorMessages\n" +
                "Source file: ${sourceFile.absolutePath}\n" +
                "Content:\n${sourceContent.lines().take(20).joinToString("\n")}"
            )
        }

        // Close the file manager
        fileManager.close()

        // Load the class using a URLClassLoader
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return classLoader.loadClass(fullClassName).kotlin
    }

    /**
     * Compiles and loads Kotlin source directly from a string.
     * This uses a temporary directory but cleans up after itself.
     * 
     * Note: True in-memory compilation with Kotlin is complex as the compiler
     * is primarily designed for file-based compilation. This implementation
     * uses a dedicated temporary directory for each compilation to minimize
     * file system impact.
     * 
     * @param src The Kotlin source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @return The loaded class
     */
    fun compileAndLoadKotlinInMemory(src: String, className: String, packageName: String): KClass<*> {
        val fullClassName = "$packageName.$className"

        // Create a dedicated temporary directory for this compilation
        val tempDir = Files.createTempDirectory("kotlin-memory").toFile()
        try {
            // Setup directories
            val sourceDir = File(tempDir, "src").apply { mkdirs() }
            val outputDir = File(tempDir, "out").apply { mkdirs() }

            // Create source file
            val packagePath = packageName.replace('.', File.separatorChar)
            val packageDir = File(sourceDir, packagePath).apply { mkdirs() }
            val sourceFile = File(packageDir, "$className.kt")
            sourceFile.writeText(src)

            // Compile using K2JVMCompiler
            val compiler = K2JVMCompiler()
            val arguments = K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = outputDir.absolutePath
                noStdlib = true
                noReflect = true
                classpath = System.getProperty("java.class.path")
                jvmTarget = System.getProperty("java.version").substringBefore(".")
            }

            val messageCollector = PrintingMessageCollector(
                System.err,
                MessageRenderer.PLAIN_FULL_PATHS,
                false
            )

            val exitCode = compiler.exec(messageCollector, Services.EMPTY, arguments)
            if (exitCode.code != 0) {
                throw RuntimeException(
                    "Kotlin compilation failed with exit code $exitCode.\n" +
                    "Source:\n${src.lines().take(20).joinToString("\n")}"
                )
            }

            // Load the class using a URLClassLoader
            val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
            return classLoader.loadClass(fullClassName).kotlin
        } finally {
            // Clean up the temporary directory
            tempDir.deleteRecursively()
        }
    }

    /**
     * Compile and load Java source directly from a string (without creating files).
     * Useful for simple cases where file persistence is not needed.
     */
    fun compileAndLoadJavaInMemory(src: String, className: String, packageName: String): KClass<*> {
        val fullClassName = "$packageName.$className"
        val outputDir = File(sessionDir, "out-memory").apply { mkdirs() }

        // Create a diagnostic collector to capture compilation errors
        val diagnostics = DiagnosticCollector<JavaFileObject>()

        // Create an in-memory source file
        val sourceObject = object : SimpleJavaFileObject(
            URI.create("string:///$className.java"), JavaFileObject.Kind.SOURCE
        ) {
            override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence = src
        }

        // Get the file manager from the compiler
        val fileManager = javaCompiler.getStandardFileManager(diagnostics, null, null)

        // Prepare compilation task with the classpath
        val options = listOf(
            "-d", outputDir.absolutePath,
            "-classpath", System.getProperty("java.class.path")
        )

        // Create a writer for compiler output
        val writer = StringWriter()

        // Execute the compiler task
        val task = javaCompiler.getTask(
            writer,
            fileManager,
            diagnostics,
            options,
            null,
            listOf(sourceObject)
        )

        val success = task.call()
        if (!success) {
            val errorMessages = diagnostics.diagnostics.joinToString("\n") { diagnostic ->
                "${diagnostic.source?.name ?: "Unknown source"}: " +
                "line ${diagnostic.lineNumber}, position ${diagnostic.columnNumber}: " +
                diagnostic.getMessage(null)
            }

            throw RuntimeException(
                "Java compilation failed:\n$errorMessages\n" +
                "Source:\n${src.lines().take(20).joinToString("\n")}"
            )
        }

        // Close the file manager
        fileManager.close()

        // Load the class using a URLClassLoader
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return classLoader.loadClass(fullClassName).kotlin
    }

}
