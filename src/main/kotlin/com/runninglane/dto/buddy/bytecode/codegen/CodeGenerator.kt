package com.runninglane.dto.buddy.bytecode.codegen

import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

interface CodeGenerator<B> {
    /**
     * Defines class structure by specifying package name, class name, modifiers, and annotations.
     * The result is a builder object that will be used in subsequent steps.
     *
     * @param baseClass The base class to implement/extend
     * @param typeParams Optional list of concrete types for generic type parameters
     * @param packageName Target package name for the generated class
     * @param className Name for the generated class
     * @return Builder object for the next step
     */
    fun defineClass(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): B

    /**
     * Implements abstract properties by adding concrete getters and setters.
     * Uses the builder from the previous step and property descriptors.
     *
     * @param builder Builder object from defineClass step
     * @param properties List of property descriptors to implement
     * @param typeParamsMapByName Optional map of type parameter names to concrete types
     * @return Updated builder for the next step
     */
    fun implementProperties(
        builder: B,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): B

    fun handleOtherAbstractMembers(
        builder: B,
        properties: List<KProperty<*>>,
        functions: List<KFunction<*>>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): B {
        if (functions.isEmpty()) return builder

        throw DtoBuddyBadInputException(
            "The following functions are not implemented properly: ${functions.map { it.name }}"
        )
    }

    fun writeToString(
        builder: B,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): String
}