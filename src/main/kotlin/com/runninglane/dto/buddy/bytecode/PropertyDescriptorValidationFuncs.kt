package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import java.lang.reflect.Modifier

/**
 * Validates that a property has consistent getter and setter
 */
internal fun PropertyDescriptor.validate() {
    if (isAlreadyCompleteAndMutable()) return

    if (shouldImplement()) {
        // Validate access modifiers of abstract getters and setters
        validateAccessModifiers()

        // Check if property has a type
        if (type == null) {
            throw DtoBuddyBadInputException(
                "Property $name in ${baseClass.name} has no type information."
            )
        }

        if (hasConcreteSetter) {
            throw DtoBuddyBadInputException(
                "Property $name in ${baseClass.name} is having concrete setter and cannot be implemented. " +
                        "Please remove the setter or make it abstract."
            )
        }

        // Check for type consistency between getter and setter
        if (getter != null && setter != null) {
            val getterType = getter.returnType
            val setterType = setter.parameterTypes[0]

            if (getterType != setterType) {
                throw DtoBuddyBadInputException(
                    "Property $name has in ${baseClass.name} inconsistent types: " +
                            "getter returns $getterType but setter accepts $setterType"
                )
            }
        }
    } else {
        if (setter != null && !hasConcreteSetter) {
            throw DtoBuddyBadInputException(
                "Property $name in ${baseClass.name} has abstract setter while getter is missing or concrete. " +
                        "Please add the abstract getter or remove the setter."
            )
        }
    }
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
                "Abstract getter ${it.name} for property $name in ${baseClass.name} must be public."
            )
        }
    }

    setter?.let {
        if (Modifier.isAbstract(it.modifiers) && !Modifier.isPublic(it.modifiers)) {
            throw DtoBuddyBadInputException(
                "Abstract setter ${it.name} for property $name in ${baseClass.name} must be public."
            )
        }
    }
}