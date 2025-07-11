package com.runninglane.dto.buddy.instance

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*

open class DefaultInstanceStrategy : InstanceStrategy {
    private val setterCache = mutableMapOf<KClass<*>, Map<String, KFunction<*>>>()

    private fun getSetters(clazz: KClass<*>): Map<String, KFunction<*>> {
        return setterCache.getOrPut(clazz) {
            val setters = mutableMapOf<String, KFunction<*>>()

            setters.putAll(
                clazz.memberProperties.filterIsInstance<KMutableProperty1<*, *>>()
                    .map { it.name to it.setter }
            )

            val getterFunctions: Map<String, KFunction<*>> = clazz.functions
                .filter { func ->
                    func.valueParameters.isEmpty() && func.returnType != Unit::class.createType()
                }
                .mapNotNull { func ->
                    val propertyName = when {
                        func.name.startsWith("get") && func.name.length > 3 ->
                            func.name.substring(3)

                        func.name.startsWith("is") && func.name.length > 2 ->
                            func.name.substring(2)

                        else -> null
                    }?.replaceFirstChar { it.lowercase() }

                    propertyName?.let { it to func }
                }
                .filterNot { (propertyName, _) -> propertyName in setters.keys }
                .toMap()

            val setterFunctions: Map<String, KFunction<*>> = clazz.functions
                .filter { func ->
                    func.valueParameters.size == 1 && func.returnType == Unit::class.createType()
                            && func.name.startsWith("set") && func.name.length > 3
                }
                .mapNotNull { func ->
                    val propertyName = func.name.substring(3).replaceFirstChar { it.lowercase() }
                    // Only count setter having consistent param type with getter
                    val getterFunction = getterFunctions[propertyName]
                    if (func.valueParameters[0].type == getterFunction?.returnType) {
                        propertyName to func
                    } else null
                }
                .toMap()

            setters.putAll(setterFunctions)

            setters.toMap()
        }
    }

    override fun create(concrete: KClass<*>): Any {
        return concrete.createInstance()
    }

    override fun populate(dto: Any, params: Map<String, Any?>) {
        if (params.isEmpty()) return

        val setters = getSetters(dto::class)

        for ((propertyName, value) in params) {
            try {
                val setter = setters[propertyName] ?: throw DtoBuddyBadInputException("No setter found")
                setter.call(dto, value)
            } catch (e: Exception) {
                if (e is DtoBuddyBadInputException) throw e

                throw DtoBuddySystemException(
                    "Failed to set property `$propertyName` on object of type ${dto::class.qualifiedName}",
                    e
                )
            }
        }
    }

}