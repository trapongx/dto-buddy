package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * The rule is the returned class must be a concrete class
 */
class ReturnedClassContractTest {
    private val dtoBuddy = DtoBuddy()

    private fun test(baseClass: KClass<*>, expectBaseClassReturned: Boolean) {
        try {
            val concreteClass = dtoBuddy.implement(baseClass)
            assertFalse(concreteClass.java.isInterface)
            assertFalse(concreteClass.isAbstract)
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
            InterfaceWithNoMember::class,
            InterfaceWithAbstractGetter::class,
            InterfaceWithAbstractGetterAndAbstractSetter::class,
            InterfaceWithAbstractProperty::class
        ).forEach { baseClass ->
            test(baseClass, false)
        }
    }

    @Test
    fun testAbstractClass() {
        listOf(
            AbstractClassWithNoMember::class,
            AbstractClassWithAbstractGetter::class,
            AbstractClassWithAbstractGetterAndAbstractSetter::class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithoutField::class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithField::class,
            AbstractClassWithAbstractImmutableProperty::class,
            AbstractClassWithAbstractMutableProperty::class,
            AbstractClassWithConcreteMutableProperty::class
        ).forEach { baseClass ->
            test(baseClass, false)
        }
    }

    @Test
    fun testConcreteClass() {
        listOf(
            ConcreteClassWithNoMember::class to true,
            ConcreteClassWithGetter::class to false,
            ConcreteClassWithSetter::class to false,
            ConcreteClassWithGetterAndSetterWithoutField::class to true,
            ConcreteClassWithGetterAndSetterWithField::class to true,
            ConcreteClassWithConcreteImmutableProperty::class to false,
            ConcreteClassWithConcreteMutableProperty::class to true
        ).forEach { (baseClass, expectBaseClassReturned) ->
            test(baseClass, expectBaseClassReturned)
        }
    }

}