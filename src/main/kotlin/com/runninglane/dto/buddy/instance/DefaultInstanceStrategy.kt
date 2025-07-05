package com.runninglane.dto.buddy.instance

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import java.lang.reflect.Method

open class DefaultInstanceStrategy : InstanceStrategy {
    private val setterCache = mutableMapOf<Class<*>, Map<String, Method>>()

    private fun getSetters(clazz: Class<*>): Map<String, Method> {
        return setterCache.getOrPut(clazz) {
            val setters = mutableMapOf<String, Method>()
            clazz.methods.filter { it.name.startsWith("set") && it.parameterCount == 1 }.groupBy {
                it.name.substring(3).replaceFirstChar { it.lowercase() }
            }.forEach { (propertyName, settersWithSameName) ->
                if (settersWithSameName.size == 1) {
                    setters.put(propertyName, settersWithSameName.first())
                } else {
                    val getterName = "get" + propertyName.replaceFirstChar { it.uppercase() }
                    val getter = clazz.methods.find { it.name == getterName && it.parameterCount == 0 }
                    if (getter != null) {
                        val setter = settersWithSameName.find { it.parameterTypes.first() == getter.returnType }
                        if (setter != null) {
                            setters.put(propertyName, setter)
                        }
                    }
                }
            }
            setters
        }
    }

    override fun create(concrete: Class<*>): Any {
        return concrete.getDeclaredConstructor().newInstance()
    }

    override fun populate(dto: Any, params: Map<String, Any?>) {
        if (params.isEmpty()) return

        val setters = getSetters(dto.javaClass)

        for ((propertyName, value) in params) {
            try {
                val setter = setters[propertyName] ?: error("No setter found")
                val convertedValue = convertValueIfNeeded(value, setter.parameterTypes.first())
                setter.invoke(dto, convertedValue)
            } catch (e: Exception) {
                throw DtoBuddyBadInputException("Failed to set property $propertyName: ${e.message}")
            }
        }
    }

    /**
     * Attempt to convert a value to the target type if needed
     */
    protected fun convertValueIfNeeded(value: Any?, targetType: Class<*>): Any? {
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
}