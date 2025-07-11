package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * The rule is the returned class must be a concrete class
 */
class GetterSetterBehaviorContractTest : TestGetSetCapability {
    private val dtoBuddy = DtoBuddy()

    private fun test(baseClass: KClass<*>, expectBaseClassReturned: Boolean, testGetSet: Boolean, testGetSetInJavaStyle: Boolean) {
        try {
            val concreteClass = dtoBuddy.implement(baseClass)
            assertFalse(concreteClass.java.isInterface)
            assertFalse(concreteClass.isAbstract)
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass)
            }
            if (testGetSet) {
                testGetSet(dtoBuddy, concreteClass, testGetSetInJavaStyle)
            }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
        }
    }

    @Test
    fun testInterface() {
        listOf(
            InterfaceWithNoMember::class ,
            InterfaceWithAbstractGetter::class ,
            InterfaceWithAbstractGetterAndAbstractSetter::class ,
            InterfaceWithAbstractProperty::class ,
        ).forEach { baseClass ->
            val testGetSet = baseClass != InterfaceWithNoMember::class
            val testGetSetInJavaStyle = testGetSet && !baseClass.simpleName!!.contains("Property")
            test(baseClass, false, testGetSet, testGetSetInJavaStyle)
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
            val testGetSet = baseClass.simpleName?.matches(Regex(".*(NoMember|Concrete).*")) != true
            val testGetSetInJavaStyle = testGetSet && !baseClass.simpleName!!.contains("Property")
            test(baseClass, expectBaseClassReturned = false, testGetSet, testGetSetInJavaStyle)
        }
    }

    @Test
    fun testConcreteClass() {
        listOf(
            ConcreteClassWithNoMember::class,
            ConcreteClassWithGetterAndSetterWithoutField::class,
            ConcreteClassWithGetterAndSetterWithField::class,
            ConcreteClassWithConcreteMutableProperty::class
        ).forEach { baseClass ->
            test(baseClass, expectBaseClassReturned = true, testGetSet = false, testGetSetInJavaStyle = false)
        }
    }

}