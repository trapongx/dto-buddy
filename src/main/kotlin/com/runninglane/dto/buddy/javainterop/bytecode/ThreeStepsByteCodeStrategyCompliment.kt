package com.runninglane.dto.buddy.javainterop.bytecode

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

/**
 * Implements the three-step bytecode generation strategy:
 * 1. Define class structure with package name, class name, modifiers, and annotations
 * 2. Implement properties by adding concrete getters and setters and handles non-property abstract functions
 * 3. Generate and load the bytecode into the runtime
 *
 * This strategy provides a structured approach to generate bytecode for DTO implementations.
 * It handles inheritance, interfaces, abstract classes and mutable/immutable properties.
 */
interface ThreeStepsByteCodeStrategyCompliment<B> : KThreeStepsByteCodeStrategyCompliment<B> {

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
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String
    ): B

    @JvmSynthetic
    override fun defineClass(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String
    ): B = defineClass(
        baseClass.java,
        typeParams?.map { it.java },
        packageName,
        className
    )

    /**
     * Implements abstract properties by adding concrete getters and setters.
     * Uses the builder from the previous step and property descriptors.
     *
     * @param builder Builder object from defineClass step
     * @param properties List of property descriptors to implement
     * @param typeParamsMapByName Optional map of type parameter names to concrete types
     * @return Updated builder for the next step
     */
    fun implementGetterSetterMethods(
        builder: B,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, Class<*>>? = null
    ): B

    @JvmSynthetic
    override fun implementProperties(
        builder: B,
        properties: List<KPropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>?
    ): B = implementGetterSetterMethods(
        builder,
        properties.filter { it.kProperty == null }.map { it.java() },
        typeParamsMapByName?.mapValues { it.value.java }
    )

    fun handleOtherAbstractMethods(
        builder: B,
        methods: List<Method>,
        typeParamsMapByName: Map<String, Class<*>>? = null
    ): B {
        return super.handleOtherAbstractMembers(
            builder,
            emptyList(),
            methods.map { it.kotlinFunction!! },
            typeParamsMapByName?.mapValues { it.value.kotlin }
        )
    }

    @JvmSynthetic
    override fun handleOtherAbstractMembers(
        builder: B,
        properties: List<KProperty<*>>,
        functions: List<KFunction<*>>,
        typeParamsMapByName: Map<String, KClass<*>>?
    ): B {
        return handleOtherAbstractMethods(builder, functions.map { it.javaMethod!! })
    }

    /**
     * Finalizes the class definition, generates the bytecode and loads it into the runtime.
     *
     * @param builder Builder object from implementProperties step
     * @param packageName Target package name for the generated class
     * @param className Name for the generated class
     * @return Generated concrete class
     */
    fun loadJavaClass(
        builder: B,
        packageName: String,
        className: String
    ): Class<*>

    @JvmSynthetic
    override fun loadClass(
        builder: B,
        packageName: String,
        className: String
    ): KClass<*> = loadJavaClass(builder, packageName, className).kotlin
}