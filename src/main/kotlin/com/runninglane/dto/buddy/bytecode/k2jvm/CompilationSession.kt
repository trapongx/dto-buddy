package com.runninglane.dto.buddy.bytecode.k2jvm

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import kotlin.reflect.KClass

class CompilationSession {
    // Keep a global shared directory for all compilations in this session
    private val sessionDir by lazy {
        val dir = Files.createTempDirectory("kotlin-session").toFile()
        dir.deleteOnExit() // Clean up on JVM shutdown
        dir
    }

    // Keep track of all source files created in this session
    private val createdSources = mutableMapOf<String, File>()

    /**
     * Compiles and loads a Kotlin source string.
     * Uses a persistent session directory to ensure all classes
     * remain available throughout the session.
     * 
     * @param src The Kotlin source code to compile
     * @param className Name of the class to load after compilation
     * @param packageName Package of the class (optional, will be extracted from source if not provided)
     * @return The loaded class
     */
    fun compileAndLoad(src: String, className: String, packageName: String): KClass<*> {
        // Get package name - either from parameter or by extracting from source
        val fullClassName = "$packageName.$className"

        // Setup directories
        val sourceDir = File(sessionDir, "src").apply { mkdirs() }
        val outputDir = File(sessionDir, "out").apply { mkdirs() }

        // Create source file
        val packageDir = File(sourceDir, fullClassName.replace('.', File.separatorChar)).apply { mkdirs() }
        val sourceFile = File(packageDir, "$className.kt")
        sourceFile.writeText(src)
        createdSources[fullClassName] = sourceFile

        // Compile using K2JVMCompiler with all source files
        val compiler = K2JVMCompiler()
        val arguments = K2JVMCompilerArguments().apply {
            freeArgs = createdSources.values.map { it.absolutePath }
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
                "Compilation failed with exit code $exitCode.\n" +
                "Source file: ${sourceFile.absolutePath}\n" +
                "Content: \n${sourceContent.lines().take(20).joinToString("\n")}"
            )
        }

        // Load the class using the standard classloader
        val classLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
        return classLoader.loadClass(fullClassName).kotlin
    }

}
