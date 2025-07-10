package com.runninglane.dto.buddy.test.cases.contract

interface InterfaceWithDefaultGetter {
    fun getName(): String = "John Doe"
}

interface InterfaceWithDefaultGetterAndAbstractSetter {
    fun getName(): String = "John Doe"
    fun setName(name: String)
}

interface InterfaceWithAbstractGetterAndDefaultSetter {
    fun getName(): String
    fun setName(name: String) { TODO("Not yet implemented") }
}

interface InterfaceWithAbstractPropertyAndDefaultGetter {
    val name: String
        get() = "John Doe"
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
