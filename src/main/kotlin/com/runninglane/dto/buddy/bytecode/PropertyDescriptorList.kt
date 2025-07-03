package com.runninglane.dto.buddy.bytecode

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.collections.iterator

internal object PropertyDescriptorList {
    /**
     * Analyzes a source class and returns information about properties to implement
     */
    fun from(sourceClass: Class<*>): List<PropertyDescriptor> {
        val methods = getAllMethods(sourceClass)
        val gettersByProperty = mutableMapOf<String, MutableList<Method>>()
        val settersByProperty = mutableMapOf<String, MutableList<Method>>()

        // Organize methods by property name and method type
        for (method in methods) {
            val methodName = method.name

            when {
                isGetter(method) -> {
                    val propertyName = extractPropertyName(methodName, "get", "is")
                    gettersByProperty.getOrPut(propertyName) { mutableListOf() }.add(method)
                }
                isSetter(method) -> {
                    val propertyName = extractPropertyName(methodName, "set")
                    settersByProperty.getOrPut(propertyName) { mutableListOf() }.add(method)
                }
            }
        }

        // Create builders with prioritized methods
        val builders = mutableMapOf<String, PropertyDescriptor.Builder>()

        // Prioritize concrete getters over abstract ones
        for ((propertyName, getters) in gettersByProperty) {
            val concreteGetter = getters.find { !Modifier.isAbstract(it.modifiers) } ?: getters.firstOrNull()
            if (concreteGetter != null) {
                val builder = builders.getOrPut(propertyName) { PropertyDescriptor.Builder(propertyName) }
                builder.getter = concreteGetter
            }
        }

        // Prioritize concrete setters over abstract ones
        for ((propertyName, setters) in settersByProperty) {
            val concreteSetter = setters.find { !Modifier.isAbstract(it.modifiers) } ?: setters.firstOrNull()
            if (concreteSetter != null) {
                val builder = builders.getOrPut(propertyName) { PropertyDescriptor.Builder(propertyName) }
                builder.setter = concreteSetter
            }
        }

        return builders.map { it.value.build() }
    }

    /**
     * Gets all methods from a class and its superclasses/interfaces
     */
    private fun getAllMethods(clazz: Class<*>): List<Method> {
        val methods = mutableListOf<Method>()

        // Add methods from the class itself
        methods.addAll(clazz.declaredMethods)

        // Add methods from interfaces
        for (`interface` in clazz.interfaces) {
            methods.addAll(getAllMethods(`interface`))
        }

        // Add methods from superclass if exists
        val superclass = clazz.superclass
        if (superclass != null && superclass != Any::class.java) {
            methods.addAll(getAllMethods(superclass))
        }

        return methods
    }

    private fun isGetter(method: Method): Boolean {
        val methodName = method.name
        val paramCount = method.parameterCount
        val returnType = method.returnType

        return (methodName.startsWith("get") && methodName.length > 3 && paramCount == 0 && returnType != Void.TYPE) ||
                (methodName.startsWith("is") && methodName.length > 2 && paramCount == 0 &&
                        (returnType == Boolean::class.java || returnType == Boolean::class.javaPrimitiveType))
    }

    private fun isSetter(method: Method): Boolean {
        val methodName = method.name
        val paramCount = method.parameterCount
        val returnType = method.returnType

        return methodName.startsWith("set") && methodName.length > 3 && paramCount == 1 &&
                (returnType == Void.TYPE || returnType == Void::class.java)
    }

    private fun extractPropertyName(methodName: String, vararg prefixes: String): String {
        for (prefix in prefixes) {
            if (methodName.startsWith(prefix) && methodName.length > prefix.length) {
                val propertyName = methodName.substring(prefix.length)
                return propertyName.first().lowercase() + propertyName.substring(1)
            }
        }
        return methodName
    }
}