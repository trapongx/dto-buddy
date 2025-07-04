package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * The rule is the returned class must be a concrete class
 */
class ReturnedClassContractTest {

    @Test
    fun testInterface() {
        listOf(
            InterfaceWithNoMember::class.java,
            InterfaceWithAbstractGetter::class.java,
            InterfaceWithAbstractGetterAndAbstractSetter::class.java,
            InterfaceWithDefaultGetter::class.java,
            InterfaceWithDefaultGetterAndAbstractSetter::class.java,
            InterfaceWithAbstractProperty::class.java,
            InterfaceWithAbstractPropertyAndDefaultGetter::class.java
        ).forEach { baseClass ->
            val createdClass = DtoBuddy.implement(baseClass)
            assertFalse(createdClass.isInterface)
            assertFalse(Modifier.isAbstract(createdClass.modifiers))
        }
    }

    @Test
    fun testAbstractClass() {
        listOf(
            AbstractClassWithNoMember::class.java,
            AbstractClassWithAbstractGetter::class.java,
            AbstractClassWithAbstractGetterAndAbstractSetter::class.java,
            AbstractClassWithConcreteGetter::class.java,
            AbstractClassWithConcreteSetter::class.java,
            AbstractClassWithConcreteGetterAndConcreteSetterWithoutField::class.java,
            AbstractClassWithConcreteGetterAndConcreteSetterWithField::class.java,
            AbstractClassWithAbstractImmutableProperty::class.java,
            AbstractClassWithAbstractMutableProperty::class.java,
            AbstractClassWithConcreteImmutableProperty::class.java,
            AbstractClassWithConcreteMutableProperty::class.java
        ).forEach { baseClass ->
            val createdClass = DtoBuddy.implement(baseClass)
            assertFalse(createdClass.isInterface)
            assertFalse(Modifier.isAbstract(createdClass.modifiers))
        }
    }

    @Test
    fun testConcreteClass() {
        listOf(
            ConcreteClassWithNoMember::class.java to true,
            ConcreteClassWithGetter::class.java to false,
            ConcreteClassWithSetter::class.java to false,
            ConcreteClassWithGetterAndSetterWithoutField::class.java to true,
            ConcreteClassWithGetterAndSetterWithField::class.java to true,
            ConcreteClassWithConcreteImmutableProperty::class.java to false,
            ConcreteClassWithConcreteMutableProperty::class.java to true
        ).forEach { (baseClass, selfComplete) ->
            val createdClass = DtoBuddy.implement(baseClass)
            assertFalse(createdClass.isInterface)
            assertFalse(Modifier.isAbstract(createdClass.modifiers))
            if (selfComplete) {
                assertEquals(baseClass, createdClass)
            }
        }
    }

}