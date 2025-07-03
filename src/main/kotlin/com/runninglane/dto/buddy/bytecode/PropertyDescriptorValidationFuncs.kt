package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import java.lang.reflect.Modifier

/**
 * Validates that a property has consistent getter and setter
 */
internal fun PropertyDescriptor.validate() {
    // Check if property has a type
    if (type == null && shouldImplement()) {
        throw DtoBuddyBadInputException(
            "Property $name has no type information."
        )
    }

    // Check if property has partially implemented accessors
    if (isPartiallyImplemented()) {
        throw DtoBuddyBadInputException(
            "Property $name has partially implemented accessors. " +
                    "Both getter and setter must be either abstract or concrete."
        )
    }

    // Check for type consistency between getter and setter
    if (getter != null && setter != null) {
        val getterType = getter.returnType
        val setterType = setter.parameterTypes[0]

        if (getterType != setterType) {
            throw DtoBuddyBadInputException(
                "Property $name has inconsistent types: " +
                        "getter returns $getterType but setter accepts $setterType"
            )
        }
    }

    // Validate access modifiers of abstract getters and setters
    validateAccessModifiers()
}

/**
 * Validates that getters and setters meet the requirements:
 * - Abstract getters and setters must be public
 * - In Kotlin, abstract methods are inherently open, but in Java they need to be explicitly checked
 */
internal fun PropertyDescriptor.validateAccessModifiers() {
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