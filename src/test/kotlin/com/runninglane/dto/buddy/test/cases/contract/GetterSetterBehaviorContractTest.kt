package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * The rule is the returned class must be a concrete class
 */
class GetterSetterBehaviorContractTest {
    private val dtoBuddy = DtoBuddy()

    private fun test(baseClass: Class<*>, expectBaseClassReturned: Boolean, testGetSet: Boolean) {
        try {
            val createdClass = dtoBuddy.implement(baseClass)
            assertFalse(createdClass.isInterface)
            assertFalse(Modifier.isAbstract(createdClass.modifiers))
            if (expectBaseClassReturned) {
                assertEquals(baseClass, createdClass)
            }
            if (testGetSet) {
                val dto = dtoBuddy.create<Any>(createdClass, mapOf("name" to "Test"))
                val getName = createdClass.getMethod("getName")
                val setName = createdClass.getMethod("setName", String::class.java)
                assertEquals("Test", getName.invoke(dto))
                setName.invoke(dto, "Test2")
                assertEquals("Test2", getName.invoke(dto))
            }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
        }
    }

    @Test
    fun testInterface() {
        listOf(
            InterfaceWithNoMember::class.java ,
            InterfaceWithAbstractGetter::class.java ,
            InterfaceWithAbstractGetterAndAbstractSetter::class.java ,
            InterfaceWithDefaultGetter::class.java ,
            InterfaceWithDefaultGetterAndAbstractSetter::class.java ,
            InterfaceWithAbstractProperty::class.java ,
            InterfaceWithAbstractPropertyAndDefaultGetter::class.java 
        ).forEach { baseClass ->
            val testGetSet = baseClass != InterfaceWithNoMember::class.java
            test(baseClass, false, testGetSet)
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
            val testGetSet = !baseClass.simpleName.matches(Regex(".*(NoMember|Concrete).*"))
            test(baseClass, expectBaseClassReturned = false, testGetSet)
        }
    }

    @Test
    fun testConcreteClass() {
        listOf(
            ConcreteClassWithNoMember::class.java,
            ConcreteClassWithGetterAndSetterWithoutField::class.java,
            ConcreteClassWithGetterAndSetterWithField::class.java,
            ConcreteClassWithConcreteMutableProperty::class.java
        ).forEach { baseClass ->
            test(baseClass, expectBaseClassReturned = true, testGetSet = false)
        }
    }

}