package com.runninglane.dto.buddy.javainterop.bytecode.codegen.tools

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeCompiler
import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeGenerator
import java.io.File
import java.util.function.Function

class StaticJavaCodeGenerator(
    outputPath: File,
    generator: JavaCodeGenerator,
    private val dataCollectorInitializer: Function<Class<*>, Any?>?
) {
    constructor(outputPath: File) : this(outputPath, JavaCodeGenerator(), null)

    constructor(outputPath: File, dataCollectorInitializer: Function<Class<*>, Any?>) : this(
        outputPath,
        JavaCodeGenerator(),
        dataCollectorInitializer
    )

    constructor(outputPath: File, generator: JavaCodeGenerator) : this(outputPath, generator, null)

    private val compiler = object : JavaCodeCompiler() {
        override fun compileAndLoadJavaClass(
            sourceCode: String,
            packageName: String,
            className: String,
            dataCollector: Any?
        ): Class<*> {
            // Create source file
            val packagePath = packageName.replace('.', File.separatorChar)
            val packageDir = File(outputPath, packagePath).apply { mkdirs() }
            val sourceFile = File(packageDir, "$className.kt")
            sourceFile.writeText(sourceCode)
            return this::class.java
        }
    }

    private val dtoBuddy = DtoBuddy(JavaCodeGenBasedByteCodeStrategy(generator, compiler))

    fun generate(dtoClasses: List<Class<*>>) {
        for (dtoClass in dtoClasses) {
            dtoBuddy.implement(
                dtoClass.kotlin,
                dataCollector = dataCollectorInitializer?.apply(dtoClass)
            )
        }
    }

    fun generate(dtoClass: Class<*>, typeParams: List<Class<*>>? = null) {
        dtoBuddy.implement(
            dtoClass.kotlin,
            typeParams?.map { it.kotlin },
            dataCollectorInitializer?.apply(dtoClass)
        )
    }
}