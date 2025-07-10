package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

/**
 * Implements the three-step bytecode generation strategy:
 * 1. Define class structure with package name, class name, modifiers, and annotations
 * 2. Implement properties by adding concrete getters and setters and handles non-property abstract functions
 * 3. Generate and load the bytecode into the runtime
 *
 * This strategy provides a structured approach to generate bytecode for DTO implementations.
 * It handles inheritance, interfaces, abstract classes and mutable/immutable properties.
 */
abstract class ThreeStepsByteCodeStrategy<B> : ByteCodeStrategy {
    // Cache for property info to avoid reanalyzing classes (for implementation)
    private val propertiesCache = mutableMapOf<KClass<*>, List<PropertyDescriptor>>()

    final override fun implement(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String
    ): KClass<*> {
        // Though cacheKey is not in classCache, it does not mean that the base class has never been analyzed before.
        // It's possible that the same baseClass passed in with different other parameters.
        val properties = propertiesCache.getOrPut(baseClass) {
            PropertyDescriptorList.from(baseClass)
        }

        val isConcreteClass = !baseClass.java.isInterface && !baseClass.isAbstract
        val isCompleteAndMutable = properties.all { it.isAlreadyCompleteAndMutable() }
        if (isConcreteClass && isCompleteAndMutable) {
            // All properties are already mutable, and it's not an interface, return the original class
            return baseClass
        }

        try {
            // Create the dynamic type builder
            val builder = defineClass(baseClass, typeParams, packageName, className)

            // Create a map of type parameter names to actual types
            val typeParamsMapByName = typeParams?.takeIf { it.isNotEmpty() }
                ?.let { typeParams ->
                    val typeParameterNames = baseClass.typeParameters.map { it.name }
                    typeParameterNames.zip(typeParams).toMap()
                } ?: emptyMap()

            // Implement properties
            val implementedBuilder = implementProperties(builder, properties, typeParamsMapByName)

            val nonPropertyAbstractFunctions = baseClass.functions.toList()
                .minus(properties.flatMap { listOfNotNull(it.getter, it.setter) })
                .filter { it.isAbstract }

            val builderWithNonPropertyAbstractFunctionsHandled = when {
                nonPropertyAbstractFunctions.isNotEmpty() ->
                    handleNonPropertyAbstractFunctions(implementedBuilder, nonPropertyAbstractFunctions)

                else -> implementedBuilder
            }

            // Load the generated class
            val generatedClass = loadClass(builderWithNonPropertyAbstractFunctionsHandled, packageName, className)

            return generatedClass
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to implement DTO: ${e.message}")
            }
        }
    }

    /**
     * Defines class structure by specifying package name, class name, modifiers, and annotations.
     * The result is a builder object that will be used in subsequent steps.
     *
     * @param baseClass The base class to implement/extend
     * @param typeParams Optional list of concrete types for generic type parameters
     * @param packageName Target package name for the generated class
     * @param className Name for the generated class
     * @return Builder object for the next step
     */
    abstract fun defineClass(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String
    ): B

    /**
     * Implements abstract properties by adding concrete getters and setters.
     * Uses the builder from the previous step and property descriptors.
     *
     * @param builder Builder object from defineClass step
     * @param properties List of property descriptors to implement
     * @param typeParamsMapByName Optional map of type parameter names to concrete types
     * @return Updated builder for the next step
     */
    abstract fun implementProperties(
        builder: B,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>? = null
    ): B

    open fun handleNonPropertyAbstractFunctions(builder: B, functions: List<KFunction<*>>): B {
        if (functions.isEmpty()) return builder

        throw DtoBuddyBadInputException(
            "The following functions are not implemented properly: ${functions.map { it.name }}"
        )
    }

    /**
     * Finalizes the class definition, generates the bytecode and loads it into the runtime.
     *
     * @param builder Builder object from implementProperties step
     * @param packageName Target package name for the generated class
     * @param className Name for the generated class
     * @return Generated concrete class
     */
    abstract fun loadClass(
        builder: B,
        packageName: String,
        className: String
    ): KClass<*>
}