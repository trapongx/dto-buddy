package com.runninglane.dto.buddy.test.cases.contract

interface InterfaceWithNoMember

interface InterfaceWithAbstractGetter {
    fun getName(): String
}

interface InterfaceWithAbstractGetterAndAbstractSetter {
    fun getName(): String
    fun setName(name: String)
}

interface InterfaceWithAbstractProperty {
    val name: String
}

abstract class AbstractClassWithNoMember

abstract class AbstractClassWithAbstractGetter {
    abstract fun getName(): String
}

abstract class AbstractClassWithAbstractGetterAndAbstractSetter {
    abstract fun getName(): String
    abstract fun setName(name: String)
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