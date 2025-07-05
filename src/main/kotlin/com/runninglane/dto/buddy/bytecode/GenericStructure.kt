package com.runninglane.dto.buddy.bytecode

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Represents the structure of a generic type
 */
internal class GenericStructure private constructor(
    val rawType: Class<*>,
    val typeParameters: List<TypeParameter> = emptyList()
) {
    data class TypeParameter(val type: Type, val position: Int)

    companion object {
        fun from(type: Type): GenericStructure? {
            return when (type) {
                is Class<*> -> GenericStructure(type)
                is ParameterizedType -> {
                    val rawType = type.rawType as Class<*>
                    val actualTypeArguments = type.actualTypeArguments
                    val parameters = actualTypeArguments.mapIndexed { index, paramType ->
                        TypeParameter(paramType, index)
                    }
                    GenericStructure(rawType, parameters)
                }
                is TypeVariable<*> -> null // Will be resolved later
                else -> null
            }
        }
    }

    /**
     * Recursively resolves a type, replacing type variables with their concrete types
     */
    private fun resolveType(type: Type, typeParamsMap: Map<String, Class<*>>): Type {
        return when (type) {
            is TypeVariable<*> -> {
                // Direct type variable (T)
                typeParamsMap[type.name] ?: type
            }
            is ParameterizedType -> {
                // Nested parameterized type (List<T>, Map<K,V>, etc)
                val rawType = type.rawType
                val resolvedArgs = type.actualTypeArguments.map { arg ->
                    resolveType(arg, typeParamsMap)
                }.toTypedArray()

                // Create a new ParameterizedType with resolved arguments
                object : ParameterizedType {
                    override fun getRawType() = rawType
                    override fun getOwnerType() = type.ownerType
                    override fun getActualTypeArguments() = resolvedArgs
                    override fun toString() = buildString {
                        append((rawType as Class<*>).simpleName)
                        append("<")
                        append(resolvedArgs.joinToString(", ") { it.toString() })
                        append(">")
                    }
                }
            }
            else -> type
        }
    }
}
