package com.runninglane.dto.buddy.bytecode.compile

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.io.StringWriter
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.*
import kotlin.reflect.KClass

class CompilationSession(
    val inMemory: Boolean = System.getProperty("dto-buddy.compilation.in-memory")?.toBoolean() ?: true
) {
    // Keep a global shared directory for all compilations in this session
    private val sessionDir by lazy {
        val dir = Files.createTempDirectory("compilation-session").toFile()
        println("dir = $dir")
        //dir.deleteOnExit() // Clean up on JVM shutdown
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
     * @return The primary loaded class
     */
    fun compileAndLoad(src: String, className: String, packageName: String, language: String = "kotlin"): Class<*> {
        // First load all classes to ensure they're available in the classloader
        val allClasses = when (language.lowercase()) {
            "kotlin" -> when (inMemory) {
                true -> loadAllClasses(src, className, packageName, this::compileKotlinInMemory)
                false -> loadAllClasses(src, className, packageName, this::compileKotlin)
            }
            "java" -> when (inMemory) {
                true -> loadAllClasses(src, className, packageName, this::compileJavaInMemory)
                false -> loadAllClasses(src, className, packageName, this::compileJava)
            }
            else -> throw IllegalArgumentException("Unsupported language: $language. Only 'kotlin' and 'java' are supported.")
        }

        // Return the main class
        val fullClassName = "$packageName.$className"
        return allClasses.find { it.name == fullClassName } 
            ?: throw RuntimeException("Main class $fullClassName not found among compiled classes")
    }

    /**
     * Compiles Kotlin source code and sets up a classloader
     * 
     * @param src The Kotlin source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @param baseDir The base directory for compilation
     * @return Pair of (ClassLoader, output directory) for loading classes
     */
    private fun compileKotlin(src: String, className: String, packageName: String, baseDir: File): Pair<URLClassLoader, File> {
        // Get package name - either from parameter or by extracting from source
        val fullClassName = "$packageName.$className"

        // Setup directories
        val sourceDir = File(baseDir, "src").apply { mkdirs() }
        val outputDir = File(baseDir, "out").apply { mkdirs() }

        // Create source file
        val packageDir = File(sourceDir, packageName.replace('.', File.separatorChar)).apply { mkdirs() }
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

        // Create classloader to load compiled classes
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return Pair(classLoader, outputDir)
    }

    /**
     * Recursively collects all classes in a directory and its subdirectories
     */
    private fun collectClassesInDirectory(dir: File, packageName: String, classLoader: ClassLoader, result: MutableList<Class<*>>) {
        dir.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> {
                    // For nested classes in subdirectories
                    val subPackage = if (packageName.isEmpty()) file.name else "$packageName.${file.name}"
                    collectClassesInDirectory(file, subPackage, classLoader, result)
                }
                file.name.endsWith(".class") -> {
                    val className = "$packageName.${file.name.substring(0, file.name.length - 6)}"
                    try {
                        result.add(classLoader.loadClass(className))
                    } catch (e: Exception) {
                        // Log error but continue with other classes
                        println("Warning: Failed to load class $className: ${e.message}")
                    }
                }
            }
        }
    }

    /**
     * Helper method to compile and load all classes using the provided compilation function
     */
    private fun loadAllClasses(
        src: String, 
        className: String, 
        packageName: String,
        compileFn: (String, String, String, File) -> Pair<URLClassLoader, File>
    ): List<Class<*>> {
        val (classLoader, outputDir) = compileFn(src, className, packageName, sessionDir)

        // Find all .class files in the output directory that match our package
        val result = mutableListOf<Class<*>>()
        val packagePath = packageName.replace('.', File.separatorChar)
        val packageDir = File(outputDir, packagePath)

        if (packageDir.exists()) {
            collectClassesInDirectory(packageDir, packageName, classLoader, result)
        }

        return result
    }

    /**
     * Compiles Java source code and sets up a classloader
     * 
     * @param src The Java source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @param baseDir The base directory for compilation
     * @return Pair of (ClassLoader, output directory) for loading classes
     */
    private fun compileJava(src: String, className: String, packageName: String, baseDir: File): Pair<URLClassLoader, File> {
        val fullClassName = "$packageName.$className"

        // Setup directories
        val sourceDir = File(baseDir, "src-java").apply { mkdirs() }
        val outputDir = File(baseDir, "out-java").apply { mkdirs() }

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

        // Create classloader to load compiled classes
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return Pair(classLoader, outputDir)
    }

    /**
     * Compiles Kotlin source directly from a string.
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
     * @param baseDir The base directory to use (ignored, will create a temp dir)
     * @return Pair of (ClassLoader, output directory) for loading classes
     */
    private fun compileKotlinInMemory(src: String, className: String, packageName: String, baseDir: File): Pair<URLClassLoader, File> {
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

            val errorStream = ByteArrayOutputStream()
            val messageCollector = PrintingMessageCollector(
                PrintStream(errorStream),
                MessageRenderer.PLAIN_FULL_PATHS,
                false
            )

            val exitCode = compiler.exec(messageCollector, Services.EMPTY, arguments)
            if (exitCode.code != 0) {
                throw RuntimeException(
                    "Kotlin compilation failed with exit code $exitCode.\n" +
                    "Compiler output:\n${String(errorStream.toByteArray())}\n" +
                    "Source:\n${src.lines().take(20).joinToString("\n")}"
                )
            }

            // Create classloader to load compiled classes
            val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
            return Pair(classLoader, outputDir)
        } catch (e: Exception) {
            tempDir.deleteRecursively()
            throw e
        }
        // NOTE: We intentionally don't delete the temporary directory in the success case,
        // because the classloader still needs to access the class files. It will be deleted
        // when the JVM exits.
    }

    /**
     * Compiles Java source directly from a string (without creating files)
     * and sets up a classloader.
     * 
     * @param src The Java source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class
     * @param baseDir The base directory for output files
     * @return Pair of (ClassLoader, output directory) for loading classes
     */
    private fun compileJavaInMemory(src: String, className: String, packageName: String, baseDir: File): Pair<URLClassLoader, File> {
        val outputDir = File(baseDir, "out-memory").apply { mkdirs() }

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

        // Create classloader to load compiled classes
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return Pair(classLoader, outputDir)
    }

}
