package com.runninglane.dto.buddy.bytecode

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Type

/**
 * Helper class to track property metadata during analysis
 */
internal data class PropertyDescriptor(
    val name: String,
    val type: Class<*>?,
    val genericType: Type?,
    val getter: Method?,
    val setter: Method?,
    val hasConcreteGetter: Boolean,
    val hasConcreteSetter: Boolean,
    val genericStructure: GenericStructure? = null
) {
    fun isPartiallyImplemented(): Boolean {
        return (getter != null && setter != null) && (hasConcreteGetter != hasConcreteSetter)
    }

    fun shouldImplement(): Boolean {
        return !hasConcreteGetter || !hasConcreteSetter
    }

    class Builder(val name: String) {
        var getter: Method? = null
        var setter: Method? = null

        fun build(): PropertyDescriptor {
            val type = getter?.returnType ?: setter?.parameterTypes?.get(0)
            val genericType = getter?.genericReturnType ?: setter?.genericParameterTypes?.get(0)
            val hasConcreteGetter = getter != null && Modifier.isAbstract(getter!!.modifiers).not()
            val hasConcreteSetter = setter != null && Modifier.isAbstract(setter!!.modifiers).not()

            // Extract generic structure information
            val genericStructure = if (genericType != null) {
                GenericStructure.from(genericType)
            } else null

            return PropertyDescriptor(
                name, type, genericType, getter, setter, hasConcreteGetter, hasConcreteSetter, genericStructure
            )
        }
    }

}