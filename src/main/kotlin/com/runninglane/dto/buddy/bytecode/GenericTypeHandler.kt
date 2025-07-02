package com.runninglane.dto.buddy.bytecode

import net.bytebuddy.description.type.TypeDescription
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Handles complex generic type operations for ByteBuddyWrapper
 * This class encapsulates logic for resolving and analyzing generic types
 */
internal class GenericTypeHandler {

    /**
     * Checks if a type contains any type variables at any nesting level
     */
    fun containsTypeVariable(type: Type): Boolean {
        return when (type) {
            is TypeVariable<*> -> true
            is ParameterizedType -> {
                // Check if any of the type arguments are type variables or contain type variables
                type.actualTypeArguments.any { argType -> 
                    when (argType) {
                        is TypeVariable<*> -> true
                        is ParameterizedType -> containsTypeVariable(argType)
                        is GenericArrayType -> containsTypeVariable(argType.genericComponentType)
                        else -> false
                    }
                }
            }
            is GenericArrayType -> containsTypeVariable(type.genericComponentType)
            else -> false
        }
    }

    /**
     * Resolves the proper type for a property, handling generic type parameters
     */
    fun resolvePropertyType(property: PropertyDescriptor, typeParamsMapByName: Map<String, Class<*>>?): Class<*> {
        if (typeParamsMapByName == null || typeParamsMapByName.isEmpty()) {
            return property.type!!
        }

        // Handle direct type variable (T)
        if (property.genericType is TypeVariable<*>) {
            val typeVar = property.genericType as TypeVariable<*>
            return typeParamsMapByName[typeVar.name] ?: property.type!!
        }

        // Handle parameterized types (List<T>, Map<K,V>)
        if (property.genericType is ParameterizedType && property.genericStructure != null) {
            // For now, we just use the raw type since ByteBuddy handles field definitions at the raw type level
            // The detailed generic info will be used when defining getter/setter methods
            return property.type!!
        }

        return property.type!!
    }

    /**
     * Creates a TypeDescription.Generic for a complex generic type with resolved type variables
     * This is needed for ByteBuddy when defining methods with generic return types or parameters
     */
    fun createGenericTypeDescription(type: Type, typeParamsMap: Map<String, Class<*>>?): TypeDescription.Generic {
        if (typeParamsMap == null || typeParamsMap.isEmpty()) {
            // If no type parameters are provided, just use the raw type
            return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(type as Class<*>)
        }

        return when (type) {
            is Class<*> -> {
                TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(type)
            }
            is TypeVariable<*> -> {
                // Resolve the type variable to its concrete type
                val resolvedType = typeParamsMap[type.name] ?: type
                if (resolvedType is Class<*>) {
                    TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(resolvedType)
                } else {
                    // If not resolved to a class, use Object as fallback
                    TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Any::class.java)
                }
            }
            is ParameterizedType -> {
                // For parameterized types, convert to a simpler approach
                val rawType = type.rawType as Class<*>

                // Extract actual type arguments and resolve type variables
                val resolvedTypeArgs = type.actualTypeArguments.map { argType ->
                    when (argType) {
                        is TypeVariable<*> -> {
                            // For type variables, use the concrete type from the map
                            typeParamsMap[argType.name] ?: Any::class.java
                        }
                        is Class<*> -> argType
                        is ParameterizedType -> {
                            // For nested parameterized types, use their raw type as fallback
                            argType.rawType as Class<*>
                        }
                        else -> Any::class.java
                    }
                }.toTypedArray()

                // Create a parameterized type description with proper Class<?> types
                TypeDescription.Generic.Builder.parameterizedType(rawType, *resolvedTypeArgs).build()
            }
            else -> {
                // For unknown types, default to Object
                TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Any::class.java)
            }
        }
    }
}
