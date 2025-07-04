package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BasePropertyConditionContractTest {

    interface InterfaceWithAbstractGetter {
        fun getName(): String
    }

    interface InterfaceWithAbstractGetterAndAbstractSetter {
        fun getName(): String
        fun setName(name: String)
    }

    interface InterfaceWithDefaultGetter {
        fun getName(): String = "John Doe"
    }

    interface InterfaceWithDefaultGetterAndAbstractSetter {
        fun getName(): String = "John Doe"
        fun setName(name: String)
    }

    interface InterfaceWithAbstractProperty {
        val name: String
    }

    interface InterfaceWithAbstractPropertyAndDefaultGetter {
        val name: String
            get() = "John Doe"
    }

    abstract class AbstractClassWithAbstractGetter {
        abstract fun getName(): String
    }

    abstract class AbstractClassWithAbstractGetterAndAbstractSetter {
        abstract fun getName(): String
        abstract fun setName(name: String)
    }

    abstract class AbstractClassWithConcreteGetterAndAbstractSetter {
        fun getName(): String = "John"
        abstract fun setName(name: String)
    }

    abstract class AbstractClassWithAbstractGetterAndConcreteSetter {
        abstract fun getName(): String
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    abstract class AbstractClassWithConcreteGetter {
        fun getName(): String = "John Doe"
    }

    abstract class AbstractClassWithConcreteSetter {
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    abstract class AbstractClassWithConcreteImmutableProperty {
        val name: String = "John Doe"
    }

    private fun assertSucceed(baseClass: Class<*>) {
        try {
            val concreteClass = DtoBuddy.implement(baseClass)
            val dto = DtoBuddy.create<Any>(concreteClass, mapOf("name" to "Test"))
            val getName = concreteClass.getMethod("getName")
            assertEquals("Test", getName.invoke(dto))
            val setName = concreteClass.getMethod("setName", String::class.java)
            setName.invoke(dto, "Test2")
            assertEquals("Test2", getName.invoke(dto))
        } catch (e: Throwable) {
            throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
        }
    }

    @Test
    fun shouldSucceedWithInterfaceHavingGetterAndOptionallySetter() {
        listOf(
            InterfaceWithAbstractGetter::class.java,
            InterfaceWithAbstractGetterAndAbstractSetter::class.java,
            InterfaceWithDefaultGetter::class.java,
            InterfaceWithDefaultGetterAndAbstractSetter::class.java,
            InterfaceWithAbstractProperty::class.java,
            InterfaceWithAbstractPropertyAndDefaultGetter::class.java
        ).forEach { baseClass ->
            assertSucceed(baseClass)
        }
    }

    @Test
    fun shouldSucceedWithAbstractClassHavingAbstractGetterAndOptionallyAbstractSetter() {
        listOf(
            AbstractClassWithAbstractGetter::class.java,
            AbstractClassWithAbstractGetterAndAbstractSetter::class.java
        ).forEach { baseClass ->
            assertSucceed(baseClass)
        }
    }

    @Test
    fun shouldSucceedWithoutPropertyImplementationWhenBaseClassIsAbstractClassHavingEitherConcreteGetterOrConcreteSetter() {
        run {
            val concreteClass = DtoBuddy.implement(AbstractClassWithConcreteGetter::class.java)
            assertTrue(concreteClass.methods.any { it.name == "getName" })
            assertFalse(concreteClass.methods.any { it.name == "setName" })
        }

        run {
            val concreteClass = DtoBuddy.implement(AbstractClassWithConcreteSetter::class.java)
            assertFalse(concreteClass.methods.any { it.name == "getName" })
            assertTrue(concreteClass.methods.any { it.name == "setName" })
        }

        run {
            val concreteClass = DtoBuddy.implement(AbstractClassWithConcreteImmutableProperty::class.java)
            assertTrue(concreteClass.methods.any { it.name == "getName" })
            assertFalse(concreteClass.methods.any { it.name == "setName" })
        }
    }

    @Test
    fun shouldFailWithAbstractClassHavingAPairOfDifferentConcretenessOfGetterAndSetter() {
        listOf(
            AbstractClassWithConcreteGetterAndAbstractSetter::class.java,
            AbstractClassWithAbstractGetterAndConcreteSetter::class.java,
        ).forEach { baseClass ->
            try {
                assertThrows<DtoBuddyBadInputException> {
                    DtoBuddy.implement(baseClass)
                }
            } catch (e: Throwable) {
                throw RuntimeException("Failed to test ${baseClass.simpleName}", e)
            }
        }
    }
}