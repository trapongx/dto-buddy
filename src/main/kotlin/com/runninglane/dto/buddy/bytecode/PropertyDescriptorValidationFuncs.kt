package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import java.lang.reflect.Modifier
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.javaField

/**
 * Validates that a property has consistent getter and setter
 */
internal fun PropertyDescriptor.validateContractCompliance() {
    if (isAlreadyCompleteAndMutable()) return

    if (shouldImplement()) {
        // Validate access modifiers of abstract getters and setters
        validateAccessModifiers()

        if (getter != null) {
            if (hasConcreteSetter) {
                throw DtoBuddyBadInputException(
                    buildString {
                        append("Property `$name` in ${baseClass.qualifiedName} is having concrete setter while getter is abstract. ")
                        append("It cannot be implemented. ")
                        append("Please remove the setter or make it abstract.")
                    }
                )
            }

            // Check for type consistency between getter and setter
            if (setter != null) {
                val getterType = getter.returnType
                val setterType = setter.parameters[1].type

                if (getterType != setterType) {
                    throw DtoBuddyBadInputException(
                        buildString {
                            append("Property `$name` in ${baseClass.qualifiedName} has inconsistent types: ")
                            append("getter returns $getterType but setter accepts $setterType")
                        }
                    )
                }
            }
        }
    } else {
        if (setter != null && !hasConcreteSetter) {
            throw DtoBuddyBadInputException(
                "Property $name in ${baseClass.qualifiedName} has abstract setter while getter is missing or concrete. " +
                        "Please add the abstract getter or remove the setter."
            )
        }
    }
}

/**
 * Validates that getters and setters meet the requirements:
 * - Abstract getters and setters must be public
 * - In Kotlin, abstract functions are inherently open, but in Java they need to be explicitly checked
 */
private fun PropertyDescriptor.validateAccessModifiers() {
    kProperty?.let {
        if (it.isAbstract && it.visibility != KVisibility.PUBLIC) {
            throw DtoBuddyBadInputException(
                "Abstract property $name in ${baseClass.qualifiedName} must be public."
            )
        }
    }

    getter?.let {
        if (it.isAbstract && it.visibility != KVisibility.PUBLIC) {
            throw DtoBuddyBadInputException(
                "Abstract getter ${it.name} for property $name in ${baseClass.qualifiedName} must be public."
            )
        }
    }

    setter?.let {
        if (it.isAbstract && it.visibility != KVisibility.PUBLIC) {
            throw DtoBuddyBadInputException(
                "Abstract setter ${it.name} for property $name in ${baseClass.qualifiedName} must be public."
            )
        }
    }
}