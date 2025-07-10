package com.runninglane.dto.buddy.bytecode

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createType
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

internal object PropertyDescriptorList {
    /**
     * Analyzes a source class and returns information about properties to implement
     */
    fun from(baseClass: KClass<*>): List<PropertyDescriptor> {
        val gettersByProperty = mutableMapOf<String, MutableList<KFunction<*>>>()
        val settersByProperty = mutableMapOf<String, MutableList<KFunction<*>>>()

        // Organize functions by property name and function type
        for (function in baseClass.functions) {
            val functionName = function.name

            when {
                isGetter(function) -> {
                    val propertyName = extractPropertyName(functionName, "get", "is")
                    gettersByProperty.getOrPut(propertyName) { mutableListOf() }.add(function)
                }
                isSetter(function) -> {
                    val propertyName = extractPropertyName(functionName, "set")
                    settersByProperty.getOrPut(propertyName) { mutableListOf() }.add(function)
                }
            }
        }

        // Create builders with prioritized functions
        val builders = mutableMapOf<String, PropertyDescriptor.Builder>()

        for ((propertyName, getters) in gettersByProperty) {
            // Prioritize concrete getters over abstract ones
            val getter = getters.find { !it.isAbstract } ?: getters.firstOrNull()
            if (getter != null) {
                val builder = builders.getOrPut(propertyName) {
                    PropertyDescriptor.Builder(baseClass, propertyName)
                }
                builder.getter = getter
            }
        }

        for ((propertyName, setters) in settersByProperty) {
            // Prioritize concrete setters over abstract ones
            val setter = setters.find { !it.isAbstract } ?: setters.firstOrNull()
            if (setter != null) {
                val builder = builders.getOrPut(propertyName) {
                    PropertyDescriptor.Builder(baseClass, propertyName)
                }
                builder.setter = setter
            }
        }

        baseClass.memberProperties.forEach { kProperty ->
            val propertyName = kProperty.name
            val builder = builders.getOrPut(propertyName) {
                PropertyDescriptor.Builder(baseClass, propertyName)
            }
            builder.kProperty = kProperty
        }

        return builders.map { it.value.build() }
    }

    private fun isGetter(function: KFunction<*>): Boolean {
        if (function.valueParameters.isNotEmpty()) return false

        if (function.returnType == Unit::class.createType()) return false

        val functionName = function.name

        if (functionName.startsWith("get") && functionName.length > 3)
            return true

        if (functionName.startsWith("is") && functionName.length > 2) {
            val returnType = function.returnType
            return returnType == Boolean::class.java || returnType == Boolean::class.javaPrimitiveType
        }

        return false
    }

    private fun isSetter(function: KFunction<*>): Boolean {
        if (function.valueParameters.size != 1) return false

        if (function.returnType != Unit::class.createType()) return false

        val functionName = function.name

        return functionName.startsWith("set") && functionName.length > 3
    }

    private fun extractPropertyName(functionName: String, vararg prefixes: String): String {
        for (prefix in prefixes) {
            if (functionName.startsWith(prefix) && functionName.length > prefix.length) {
                return functionName.substring(prefix.length)
                    .replaceFirstChar { it.lowercase() }
            }
        }
        return functionName
    }
}