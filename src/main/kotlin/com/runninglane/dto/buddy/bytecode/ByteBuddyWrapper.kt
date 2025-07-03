package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import com.runninglane.dto.buddy.util.capitalize
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.matcher.ElementMatchers
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable

/**
 * A wrapper around ByteBuddy to simplify its usage for DTO generation
 */
internal class ByteBuddyWrapper {
    private val byteBuddy = ByteBuddy()
    private val genericTypeHandler = GenericTypeHandler()

    /**
     * Create a dynamic type builder based on the source class
     * - For interfaces: creates a class implementing the interface
     * - For abstract classes: creates a subclass of the abstract class
     * - For concrete classes: creates a subclass with all properties made mutable
     */
    fun createDynamicType(
        sourceClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String
    ): DynamicType.Builder<*> {
        // Validate that base class is public
        validateBaseClass(sourceClass)
        val typeDesc = if (typeParams != null && typeParams.isNotEmpty()) {
            TypeDescription.Generic.Builder.parameterizedType(
                sourceClass,
                *typeParams.toTypedArray()
            )
        } else {
            TypeDescription.Generic.Builder.rawType(sourceClass)
        }.build()

        return when {
            sourceClass.isInterface -> {
                byteBuddy
                    .subclass(Any::class.java)
                    .implement(typeDesc)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }

            Modifier.isAbstract(sourceClass.modifiers) -> {
                byteBuddy
                    .subclass(typeDesc)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }

            else -> {
                // For concrete classes, create a subclass that makes all properties mutable
                byteBuddy
                    .subclass(sourceClass)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }
        }
    }

    /**
     * Adds fields and accessors for all the properties
     * @param typeParamsMapByName Optional map of type parameter names to their actual types for generic classes
     */
    fun implementProperties(
        builder: DynamicType.Builder<*>,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, Class<*>>? = null
    ): DynamicType.Builder<*> {
        var resultBuilder = builder

        properties.forEach { it.validate() }

        properties.filter { it.shouldImplement() }

        for (property in properties) {
            property.type!! // after validated and filtered by shouldImplement(), it is surely not null

            // Resolve the property type based on its generic structure
            val resolvedType = genericTypeHandler.resolvePropertyType(property, typeParamsMapByName)

            // Define field
            resultBuilder = resultBuilder.defineField(
                property.name, resolvedType, Visibility.PRIVATE
            )

            // Implement getter if needed
            if (property.getter != null) {
                if (property.genericType is TypeVariable<*> || 
                    (property.genericType is ParameterizedType && genericTypeHandler.containsTypeVariable(property.genericType))) {
                    // For all generic types (simple or complex), define a new method with the correctly resolved return type
                    val getterName = property.getter.name
                    resultBuilder = resultBuilder.defineMethod(getterName, resolvedType, Visibility.PUBLIC)
                        .intercept(FieldAccessor.ofField(property.name))
                        .annotateMethod(Override())
                } else {
                    // For non-generic types, we can just intercept the existing method
                    resultBuilder = resultBuilder.method(ElementMatchers.`is`(property.getter))
                        .intercept(FieldAccessor.ofField(property.name))
                        .annotateMethod(Override())
                }
            }

            // Implement setter if needed
            if (property.setter != null) {
                if (property.genericType is TypeVariable<*> || 
                    (property.genericType is ParameterizedType && genericTypeHandler.containsTypeVariable(property.genericType))) {
                    // For all generic types (simple or complex), define a new method with correctly resolved parameter type
                    val setterName = property.setter.name
                    resultBuilder = resultBuilder.defineMethod(setterName, Void.TYPE, Visibility.PUBLIC)
                        .withParameter(resolvedType)
                        .intercept(FieldAccessor.ofField(property.name))
                        .annotateMethod(Override())
                } else {
                    // For non-generic types, we can just intercept the existing method
                    resultBuilder = resultBuilder.method(ElementMatchers.`is`(property.setter))
                        .intercept(FieldAccessor.ofField(property.name))
                        .annotateMethod(Override())
                }
            } else {
                // Always add a setter, even for read-only properties in the original interface
                // This follows the requirement to make all properties mutable
                val setterName = "set" + property.name.capitalize()
                resultBuilder = resultBuilder.defineMethod(setterName, Void.TYPE, Visibility.PUBLIC)
                    .withParameter(resolvedType)
                    .intercept(FieldAccessor.ofField(property.name))
            }
        }

        return resultBuilder
    }

    /**
     * Loads the generated class
     */
    fun loadClass(builder: DynamicType.Builder<*>): Class<*> {
        try {
            return builder.make()
                .load(javaClass.classLoader)
                .loaded
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to load generated class: ${e.message}")
        }
    }

    /**
     * Creates a new instance of the specified class
     */
    fun <T> createInstance(clazz: Class<*>): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return clazz.getDeclaredConstructor().newInstance() as T
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to create instance: ${e.message}")
        }
    }

    /**
     * Populates object properties with values from a map
     */
    fun populate(instance: Any, properties: List<PropertyDescriptor>, params: Map<String, Any?>) {
        for ((propertyName, value) in params) {
            val property = properties.find { it.name == propertyName } ?: continue
            if (property.setter != null) {
                try {
                    // Check if value type is compatible with property type
                    val convertedValue = convertValueIfNeeded(value, property.type!!)
                    property.setter.invoke(instance, convertedValue)
                } catch (e: Exception) {
                    throw DtoBuddySystemException("Failed to set property $propertyName: ${e.message}")
                }
            }
        }
    }

    /**
     * Attempt to convert a value to the target type if needed
     */
    private fun convertValueIfNeeded(value: Any?, targetType: Class<*>): Any? {
        if (value == null) return null

        // If value is already of the correct type, return it
        if (value::class.java == targetType || targetType.isAssignableFrom(value::class.java)) {
            return value
        }

        // Handle primitive conversions
        return when (targetType) {
            Int::class.java, Integer::class.java -> (value as? Number)?.toInt() ?: value.toString().toInt()
            Long::class.java -> (value as? Number)?.toLong() ?: value.toString().toLong()
            Double::class.java -> (value as? Number)?.toDouble() ?: value.toString().toDouble()
            Float::class.java -> (value as? Number)?.toFloat() ?: value.toString().toFloat()
            Boolean::class.java -> (value as? Boolean) ?: value.toString().toBoolean()
            String::class.java -> value.toString()
            else -> throw DtoBuddySystemException("Cannot convert ${value::class.java} to $targetType")
        }
    }

    /**
     * Validates that a base class meets the requirements:
     * - Must be public
     * - If class (not interface), must be open/abstract
     */
    private fun validateBaseClass(baseClass: Class<*>) {
        if (!Modifier.isPublic(baseClass.modifiers)) {
            throw DtoBuddyBadInputException(
                "Base class ${baseClass.name} must be public."
            )
        }

        // For classes (not interfaces), check if they are open/abstract and not final
        if (Modifier.isFinal(baseClass.modifiers)) {
            throw DtoBuddyBadInputException(
                "Base class ${baseClass.name} must be not be final class."
            )
        }
    }
}
