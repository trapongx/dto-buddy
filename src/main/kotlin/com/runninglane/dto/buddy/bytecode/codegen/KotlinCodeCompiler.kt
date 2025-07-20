package com.runninglane.dto.buddy.bytecode.codegen

import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import kotlin.reflect.KClass

/**
 * Implementation of ByteCodeStrategy that uses KotlinPoet to generate source code
 * and then compiles it using the embedded Kotlin compiler.
 */
open class KotlinCodeCompiler : CodeCompiler {

    /**
     * Generates Kotlin source code and compiles it using the CompilationSession
     */
    override fun compileAndLoadClass(
        sourceCode: String,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): KClass<*> {
        try {
            return CompilationSession.compileAndLoad(sourceCode, className, packageName).kotlin
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to compile and load generated class: ${e.message}", e)
        }
    }

}
