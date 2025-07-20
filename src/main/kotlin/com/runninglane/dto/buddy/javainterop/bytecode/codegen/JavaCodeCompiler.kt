package com.runninglane.dto.buddy.javainterop.bytecode.codegen

import com.runninglane.dto.buddy.bytecode.codegen.CompilationSession
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import com.runninglane.dto.buddy.javainterop.bytecode.KCodeCompiler
import kotlin.reflect.KClass

open class JavaCodeCompiler : KCodeCompiler {
    @JvmSynthetic
    override fun compileAndLoadClass(
        sourceCode: String,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): KClass<*> {
        return compileAndLoadJavaClass(sourceCode, packageName, className, dataCollector).kotlin
    }

    open fun compileAndLoadJavaClass(
        sourceCode: String,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): Class<*> {
        try {
            // Compile and load the generated Java class with the known class name
            return CompilationSession.compileAndLoad(sourceCode, className, packageName, "java")
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to compile and load generated Java class: ${e.message}", e)
        }
    }
}