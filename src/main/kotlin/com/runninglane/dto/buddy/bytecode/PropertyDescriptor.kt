package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import kotlin.reflect.*

/**
 * Helper class to track property metadata during analysis
 */
data class PropertyDescriptor(
    val baseClass: KClass<*>,
    val name: String,
    val type: KType,
    val isNullable: Boolean,
    val kProperty: KProperty<*>?,
    val getter: KFunction<*>?,
    val setter: KFunction<*>?,
    val hasConcreteGetter: Boolean,
    val hasConcreteSetter: Boolean,
) {
    fun isAlreadyCompleteAndMutable(): Boolean {
        return when {
            kProperty != null -> !kProperty.isAbstract && kProperty is KMutableProperty1<*, *>
            else -> getter != null && setter != null && hasConcreteGetter && hasConcreteSetter
        }
    }

    fun shouldImplement(): Boolean {
        return when {
            kProperty != null -> kProperty.isAbstract
            else -> getter != null && !hasConcreteGetter
        }
    }

    class Builder(val baseClass: KClass<*>, val name: String) {
        var kProperty: KProperty<*>? = null
        var getter: KFunction<*>? = null
        var setter: KFunction<*>? = null

        fun build(): PropertyDescriptor {
            val type = kProperty?.returnType
                ?: getter?.returnType
                ?: setter?.parameters?.get(0)?.type
                ?: throw DtoBuddySystemException("Failed to determine property type for `$name` in ${baseClass.qualifiedName}")

            val isNullable: Boolean =  type.isMarkedNullable || type.toString().endsWith("!")

            val hasConcreteGetter = getter?.isAbstract == false
            val hasConcreteSetter = setter?.isAbstract == false

            return PropertyDescriptor(
                baseClass, name,
                type,
                isNullable,
                kProperty,
                getter, setter,
                hasConcreteGetter, hasConcreteSetter
            )
        }
    }

}