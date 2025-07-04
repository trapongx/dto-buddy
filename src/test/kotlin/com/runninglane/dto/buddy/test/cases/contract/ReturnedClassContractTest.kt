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
    private val dtoBuddy = DtoBuddy()

    private fun test(baseClass: Class<*>, expectBaseClassReturned: Boolean) {
        try {
            val concreteClass = dtoBuddy.implement(baseClass)
            assertFalse(concreteClass.isInterface)
            assertFalse(Modifier.isAbstract(concreteClass.modifiers))
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass)
            }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
        }
    }

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
            test(baseClass, false)
        }
    }

    @Test
    fun testAbstractClass() {
        listOf(
            AbstractClassWithNoMember::class.java,
            AbstractClassWithAbstractGetter::class.java,
            AbstractClassWithAbstractGetterAndAbstractSetter::class.java,
            AbstractClassWithConcreteGetterAndConcreteSetterWithoutField::class.java,
            AbstractClassWithConcreteGetterAndConcreteSetterWithField::class.java,
            AbstractClassWithAbstractImmutableProperty::class.java,
            AbstractClassWithAbstractMutableProperty::class.java,
            AbstractClassWithConcreteMutableProperty::class.java
        ).forEach { baseClass ->
            test(baseClass, false)
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
        ).forEach { (baseClass, expectBaseClassReturned) ->
            test(baseClass, expectBaseClassReturned)
        }
    }

}