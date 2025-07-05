package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Implements the three-step bytecode generation strategy:
 * 1. Define class structure with package name, class name, modifiers, and annotations
 * 2. Implement properties by adding concrete getters and setters and handles non-property abstract methods
 * 3. Generate and load the bytecode into the runtime
 *
 * This strategy provides a structured approach to generate bytecode for DTO implementations.
 * It handles inheritance, interfaces, abstract classes and mutable/immutable properties.
 */
abstract class ThreeStepsByteCodeStrategy<B> : ByteCodeStrategy {
    // Cache for property info to avoid reanalyzing classes (for implementation)
    private val propertiesCache = mutableMapOf<Class<*>, List<PropertyDescriptor>>()

    final override fun implement(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String
    ): Class<*> {
        // Though cacheKey is not in classCache, it does not mean that the base class has never been analyzed before.
        // It's possible that the same baseClass passed in with different other parameters.
        val properties = propertiesCache.getOrPut(baseClass) {
            PropertyDescriptorList.from(baseClass)
        }

        val isConcreteClass = !baseClass.isInterface && !Modifier.isAbstract(baseClass.modifiers)
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

            val nonPropertyAbstractMethods = baseClass.methods.toList()
                .minus(properties.flatMap { listOfNotNull(it.getter, it.setter) })
                .filter { Modifier.isAbstract(it.modifiers) || it.declaringClass.isInterface }

            val builderWithNonPropertyAbstractMethodsHandled = when {
                nonPropertyAbstractMethods.isNotEmpty() ->
                    handleNonPropertyAbstractMethods(implementedBuilder, nonPropertyAbstractMethods)

                else -> implementedBuilder
            }

            // Load the generated class
            val generatedClass = loadClass(builderWithNonPropertyAbstractMethodsHandled)

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
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
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
        typeParamsMapByName: Map<String, Class<*>>? = null
    ): B

    open fun handleNonPropertyAbstractMethods(builder: B, methods: List<Method>): B {
        if (methods.isEmpty()) return builder

        throw DtoBuddyBadInputException(
            "The following methods are not implemented properly: ${methods.map { it.name }}"
        )
    }

    /**
     * Finalizes the class definition, generates the bytecode and loads it into the runtime.
     *
     * @param builder Builder object from implementProperties step
     * @return Generated concrete class
     */
    abstract fun loadClass(builder: B): Class<*>
}