package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReturnedClassContractTest {

    interface InterfaceWithNoMember

    interface InterfaceWithAbstractGetter {
        fun getName(): String
    }

    interface InterfaceWithAbstractSetter {
        fun setName(name: String)
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

    abstract class AbstractClassWithNoMember

    abstract class AbstractClassWithAbstractGetter {
        abstract fun getName(): String
    }

    abstract class AbstractClassWithAbstractSetter {
        abstract fun setName(name: String)
    }

    abstract class AbstractClassWithAbstractGetterAndAbstractSetter {
        abstract fun getName(): String
        abstract fun setName(name: String)
    }

    abstract class AbstractClassWithConcreteGetter {
        fun getName(): String = "John Doe"
    }

    abstract class AbstractClassWithConcreteSetter {
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    abstract class AbstractClassWithConcreteGetterAndConcreteSetterWithoutField {
        fun getName(): String = "John Doe"
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    abstract class AbstractClassWithConcreteGetterAndConcreteSetterWithField {
        private var _name: String = "John Doe"
        fun getName(): String = _name
        fun setName(name: String) { this._name = name }
    }

    abstract class AbstractClassWithAbstractImmutableProperty {
        abstract val name: String
    }

    abstract class AbstractClassWithAbstractMutableProperty {
        abstract var name: String
    }

    abstract class AbstractClassWithConcreteImmutableProperty {
        val name: String = "John Doe"
    }

    abstract class AbstractClassWithConcreteMutableProperty {
        var name: String = "John Doe"
    }
    
    open class ConcreteClassWithNoMember

    open class ConcreteClassWithGetter {
        fun getName(): String = "John Doe"
    }

    open class ConcreteClassWithSetter {
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    open class ConcreteClassWithGetterAndSetterWithoutField {
        fun getName(): String = "John Doe"
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    open class ConcreteClassWithGetterAndSetterWithField {
        private var _name: String = "John Doe"
        fun getName(): String = _name
        fun setName(name: String) { this._name = name }
    }

    open class ConcreteClassWithConcreteImmutableProperty {
        val name: String = "John Doe"
    }

    open class ConcreteClassWithConcreteMutableProperty {
        var name: String = "John Doe"
    }

    @Test
    fun testInterface() {
        listOf(
            InterfaceWithNoMember::class.java,
            InterfaceWithAbstractGetter::class.java,
            InterfaceWithAbstractSetter::class.java,
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
            AbstractClassWithAbstractSetter::class.java,
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