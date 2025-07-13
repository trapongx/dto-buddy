package com.runninglane.dto.buddy.javainterop.bytecode

import kotlin.reflect.KClass

/**
 * Implements the three-step bytecode generation strategy:
 * 1. Define class structure with package name, class name, modifiers, and annotations
 * 2. Implement properties by adding concrete getters and setters and handles non-property abstract functions
 * 3. Generate and load the bytecode into the runtime
 *
 * This strategy provides a structured approach to generate bytecode for DTO implementations.
 * It handles inheritance, interfaces, abstract classes and mutable/immutable properties.
 */
class ThreeStepsByteCodeStrategy<B>(compliment: ThreeStepsByteCodeStrategyCompliment<B>) : KThreeStepsByteCodeStrategy<B>(compliment), ByteCodeStrategy {

    override fun implement(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): Class<*> = super<KThreeStepsByteCodeStrategy>.implement(
        baseClass.kotlin,
        typeParams?.map { it.kotlin },
        packageName,
        className,
        dataCollector
    ).java

    @JvmSynthetic
    override fun implement(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): KClass<*> = super<KThreeStepsByteCodeStrategy>.implement(
        baseClass,
        typeParams,
        packageName,
        className,
        dataCollector
    )

}