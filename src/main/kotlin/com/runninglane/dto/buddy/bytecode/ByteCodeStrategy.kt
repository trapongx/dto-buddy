package com.runninglane.dto.buddy.bytecode

import kotlin.reflect.KClass

interface ByteCodeStrategy {
    /**
     * Generate a concrete mutable DTO class based on the base class
     * - For interfaces: creates a class implementing the interface with all properties made mutable
     * - For abstract classes: creates a subclass of the abstract class with all properties made mutable
     * - For concrete classes have immutable properties: creates a subclass with all properties made mutable
     * - For concrete classes having no immutable properties: return the baseClass itself
     */
    fun implement(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): KClass<*>
}