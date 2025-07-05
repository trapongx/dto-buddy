package com.runninglane.dto.buddy.bytecode

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
            val typeVar = property.genericType
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

}
