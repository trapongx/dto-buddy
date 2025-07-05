package com.runninglane.dto.buddy.bytecode.bytebuddy

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.PropertyDescriptorList
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import java.lang.reflect.Modifier

class ByteBuddyByteCodeStrategy(val byteBuddyWrapper: ByteBuddyWrapper) : ByteCodeStrategy {

    constructor() : this(ByteBuddyWrapper())

    // Cache for property info to avoid reanalyzing classes (for implementation)
    private val propertiesCache = mutableMapOf<Class<*>, List<PropertyDescriptor>>()

    override fun implement(
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
            val builder = byteBuddyWrapper.createDynamicType(baseClass, typeParams, packageName, className)

            // Create a map of type parameter names to actual types
            val typeParamsMapByName = typeParams?.takeIf { it.isNotEmpty() }
                ?.let { typeParams ->
                    val typeParameterNames = baseClass.typeParameters.map { it.name }
                    typeParameterNames.zip(typeParams).toMap()
                } ?: emptyMap()

            // Implement properties
            val implementedBuilder = byteBuddyWrapper.implementProperties(builder, properties, typeParamsMapByName)

            // Load the generated class
            val generatedClass = byteBuddyWrapper.loadClass(implementedBuilder)

            return generatedClass
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to implement DTO: ${e.message}")
            }
        }
    }
}