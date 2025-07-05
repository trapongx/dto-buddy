package com.runninglane.dto.buddy.bytecode.bytebuddy

import com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
import com.runninglane.dto.buddy.bytecode.GenericTypeHandler
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.validateContractCompliance
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
open class ByteBuddyWrapper {
    private val byteBuddy = ByteBuddy()
    private val genericTypeHandler = GenericTypeHandler()

    /**
     * Create a dynamic type builder based on the source class
     * - For interfaces: creates a class implementing the interface
     * - For abstract classes: creates a subclass of the abstract class
     * - For concrete classes: creates a subclass with all properties made mutable
     */
    open fun createDynamicType(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String
    ): DynamicType.Builder<*> {
        // Validate that base class is public
        validateBaseClass(baseClass)
        val typeDesc = if (typeParams != null && typeParams.isNotEmpty()) {
            TypeDescription.Generic.Builder.parameterizedType(
                baseClass,
                *typeParams.toTypedArray()
            )
        } else {
            TypeDescription.Generic.Builder.rawType(baseClass)
        }.build()

        return when {
            baseClass.isInterface -> {
                byteBuddy
                    .subclass(Any::class.java)
                    .implement(typeDesc)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }

            Modifier.isAbstract(baseClass.modifiers) -> {
                byteBuddy
                    .subclass(typeDesc)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }

            else -> {
                // For concrete classes, create a subclass that makes all properties mutable
                byteBuddy
                    .subclass(baseClass)
                    .name("$packageName.$className")
                    .annotateType(DtoBuddyGenerated())
            }
        }
    }

    /**
     * Adds fields and accessors for all the properties
     * @param typeParamsMapByName Optional map of type parameter names to their actual types for generic classes
     */
    open fun implementProperties(
        builder: DynamicType.Builder<*>,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, Class<*>>? = null
    ): DynamicType.Builder<*> {
        var resultBuilder = builder

        properties.forEach { it.validateContractCompliance() }

        for (property in properties.filter { it.shouldImplement() }) {
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
                    // For all generic types (simple or complex), define a new method with the correctly resolved parameter type
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
    open fun loadClass(builder: DynamicType.Builder<*>): Class<*> {
        try {
            return builder.make()
                .load(javaClass.classLoader)
                .loaded
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to load generated class: ${e.message}")
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
                "Base class ${baseClass.name} must not be final class."
            )
        }
    }
}
