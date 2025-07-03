package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
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

    /**
     * Validates that getters and setters meet the requirements:
     * - Abstract getters and setters must be public
     * - In Kotlin, abstract methods are inherently open, but in Java they need to be explicitly checked
     */
    fun validateAccessModifiers() {
        getter?.let {
            if (Modifier.isAbstract(it.modifiers) && !Modifier.isPublic(it.modifiers)) {
                throw DtoBuddyBadInputException(
                    "Abstract getter ${it.name} for property $name must be public."
                )
            }
        }

        setter?.let {
            if (Modifier.isAbstract(it.modifiers) && !Modifier.isPublic(it.modifiers)) {
                throw DtoBuddyBadInputException(
                    "Abstract setter ${it.name} for property $name must be public."
                )
            }
        }
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