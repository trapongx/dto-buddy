package com.runninglane.dto.buddy.bytecode.codegen.tools

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.CodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.runninglane.dto.buddy.naming.DefaultNamingStrategy
import com.runninglane.dto.buddy.naming.NamingStrategy
import java.io.File
import kotlin.reflect.KClass

class StaticKotlinCodeGenerator(
    outputPath: File,
    namingStrategy: NamingStrategy = DefaultNamingStrategy(),
    generator: KotlinCodeGenerator = KotlinCodeGenerator(),
    private val dataCollectorInitializer: ((KClass<*>) -> Any?)? = null
) {
    private val compiler = object : CodeCompiler {
        override fun compileAndLoadClass(
            sourceCode: String,
            packageName: String,
            className: String,
            dataCollector: Any?
        ): KClass<*> {
            // Create source file
            val packagePath = packageName.replace('.', File.separatorChar)
            val packageDir = File(outputPath, packagePath).apply { mkdirs() }
            val sourceFile = File(packageDir, "$className.kt")
            sourceFile.writeText(sourceCode)
            return Any::class
        }
    }

    private val dtoBuddy = DtoBuddy(
        namingStrategy = namingStrategy,
        byteCodeStrategy = CodeGenBasedByteCodeStrategy(generator, compiler)
    )

    fun generate(dtoClasses: List<KClass<*>>) {
        for (dtoClass in dtoClasses) {
            dtoBuddy.implement(
                dtoClass,
                dataCollector = dataCollectorInitializer?.invoke(dtoClass)
            )
        }
    }

    fun generate(dtoClass: KClass<*>, typeParams: List<KClass<*>>? = null) {
        dtoBuddy.implement(
            dtoClass,
            typeParams,
            dataCollectorInitializer?.invoke(dtoClass)
        )
    }
}