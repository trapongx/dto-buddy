package com.runninglane.dto.buddy.bytecode.codegen

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.PropertyDescriptorList
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

/**
 * Implements the three-step bytecode generation strategy:
 * 1. Define class structure with package name, class name, modifiers, and annotations
 * 2. Implement properties by adding concrete getters and setters and handles non-property abstract functions
 * 3. Generate and load the bytecode into the runtime
 *
 * This strategy provides a structured approach to generate bytecode for DTO implementations.
 * It handles inheritance, interfaces, abstract classes and mutable/immutable properties.
 */
open class CodeGenBasedByteCodeStrategy<B>(
    private val generator: CodeGenerator<B>,
    private val compiler: CodeCompiler
) : ByteCodeStrategy {
    // Cache for property info to avoid reanalyzing classes (for implementation)
    private val propertiesCache = mutableMapOf<KClass<*>, List<PropertyDescriptor>>()

    override fun implement(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): KClass<*> {
        val isConcreteClass = !baseClass.java.isInterface && !baseClass.isAbstract
        if (isConcreteClass) {
            return baseClass
        }

        try {
            val sourceCode = generateSourceCode(baseClass, typeParams, packageName, className, dataCollector)

            // Load the generated class
            val generatedClass = compiler.compileAndLoadClass(sourceCode, packageName, className, dataCollector)

            return generatedClass
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException(
                    "Failed to implement DTO: ${e.message}, dataCollector: $dataCollector",
                    e
                )
            }
        }
    }

    private fun generateSourceCode(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): String {
        // Though cacheKey is not in classCache, it does not mean that the base class has never been analyzed before.
        // It's possible that the same baseClass passed in with different other parameters.
        val properties = propertiesCache.getOrPut(baseClass) {
            PropertyDescriptorList.from(baseClass)
        }

        try {
            // Create the dynamic type builder
            var updatedBuilder = generator.defineClass(baseClass, typeParams, packageName, className, dataCollector)

            // Create a map of type parameter names to actual types
            val typeParamsMapByName = typeParams?.takeIf { it.isNotEmpty() }
                ?.let { typeParams ->
                    val typeParameterNames = baseClass.typeParameters.map { it.name }
                    typeParameterNames.zip(typeParams).toMap()
                } ?: emptyMap()

            // Implement properties
            updatedBuilder = generator.implementProperties(updatedBuilder, properties, typeParamsMapByName, dataCollector)

            val nonPublicAbstractProperties = baseClass.memberProperties.toList()
                .minus(properties.mapNotNull { it.kProperty })
                .filter { it.isAbstract }

            val nonPropertyAbstractFunctions = baseClass.functions.toList()
                .minus(properties.flatMap { listOfNotNull(it.getter, it.setter) })
                .filter { it.isAbstract }

            updatedBuilder = when {
                nonPublicAbstractProperties.isNotEmpty() || nonPropertyAbstractFunctions.isNotEmpty() ->
                    generator.handleOtherAbstractMembers(
                        updatedBuilder,
                        nonPublicAbstractProperties,
                        nonPropertyAbstractFunctions,
                        typeParamsMapByName,
                        dataCollector
                    )

                else -> updatedBuilder
            }

            return generator.writeToString(updatedBuilder, packageName, className, dataCollector)
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException(
                    "Failed to implement DTO: ${e.message}, dataCollector: $dataCollector",
                    e
                )
            }
        }
    }
}