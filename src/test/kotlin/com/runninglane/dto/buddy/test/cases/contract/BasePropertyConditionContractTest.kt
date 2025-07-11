package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.test.*

class BasePropertyConditionContractTest : TestGetSetCapability {
    private val dtoBuddy = DtoBuddy()

    private fun assertSucceed(baseClass: KClass<*>, testGetSetInJavaStyle: Boolean) {
        try {
            val concreteClass = dtoBuddy.implement(baseClass)
            testGetSet(dtoBuddy, concreteClass, testGetSetInJavaStyle)
        } catch (e: Throwable) {
            throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
        }
    }

    @Test
    fun shouldSucceedWithInterfaceHavingAbstractGetterAndOptionallySetter() {
        listOf(
            InterfaceWithAbstractGetter::class,
            InterfaceWithAbstractGetterAndAbstractSetter::class,
            InterfaceWithAbstractProperty::class
        ).forEach { baseClass ->
            val testGetSetInJavaStyle = !baseClass.simpleName!!.contains("Property")
            assertSucceed(baseClass, testGetSetInJavaStyle)
        }
    }

    @Test
    fun shouldSucceedWithAbstractClassHavingAbstractGetterAndOptionallyAbstractSetter() {
        listOf(
            AbstractClassWithAbstractGetter::class,
            AbstractClassWithAbstractGetterAndAbstractSetter::class
        ).forEach { baseClass ->
            assertSucceed(baseClass, testGetSetInJavaStyle = true)
        }
    }

    @Test
    fun shouldSucceedWithoutPropertyImplementationWhenBaseClassIsInterfaceOrAbstractClassHavingEitherConcreteGetterOrConcreteSetter() {
        run {
            val concreteClass = dtoBuddy.implement(InterfaceWithDefaultGetter::class)
            assertFalse(concreteClass.memberProperties.any { it.name == "name" })
            assertTrue(concreteClass.functions.any { it.name == "getName" })
            assertFalse(concreteClass.functions.any { it.name == "setName" })
        }

        run {
            val concreteClass = dtoBuddy.implement(InterfaceWithAbstractPropertyAndDefaultGetter::class)
            val property = concreteClass.memberProperties.find { it.name == "name" }
            assertNotNull(property)
            assertIsNot<KMutableProperty1<*, *>>(property)
            assertFalse(concreteClass.functions.any { it.name == "getName" })
            assertFalse(concreteClass.functions.any { it.name == "setName" })
        }

        run {
            val concreteClass = dtoBuddy.implement(AbstractClassWithConcreteGetter::class)
            assertFalse(concreteClass.memberProperties.any { it.name == "name" })
            assertTrue(concreteClass.functions.any { it.name == "getName" })
            assertFalse(concreteClass.functions.any { it.name == "setName" })
        }

        run {
            val concreteClass = dtoBuddy.implement(AbstractClassWithConcreteSetter::class)
            assertFalse(concreteClass.memberProperties.any { it.name == "name" })
            assertFalse(concreteClass.functions.any { it.name == "getName" })
            assertTrue(concreteClass.functions.any { it.name == "setName" })
        }

        run {
            val concreteClass = dtoBuddy.implement(AbstractClassWithConcreteImmutableProperty::class)
            val property = concreteClass.memberProperties.find { it.name == "name" }
            assertNotNull(property)
            assertIsNot<KMutableProperty1<*, *>>(property)
            assertFalse(concreteClass.functions.any { it.name == "getName" })
            assertFalse(concreteClass.functions.any { it.name == "setName" })
        }
    }

    @Test
    fun shouldFailWithAbstractClassHavingAPairOfDifferentConcretenessOfGetterAndSetter() {
        listOf(
            InterfaceWithDefaultGetterAndAbstractSetter::class ,
            InterfaceWithAbstractGetterAndDefaultSetter::class ,
            AbstractClassWithConcreteGetterAndAbstractSetter::class,
            AbstractClassWithAbstractGetterAndConcreteSetter::class,
        ).forEach { baseClass ->
            try {
                assertThrows<DtoBuddyBadInputException> {
                    dtoBuddy.implement(baseClass)
                }
            } catch (e: Throwable) {
                throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
            }
        }
    }

}