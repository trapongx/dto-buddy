package com.runninglane.dto.buddy.javainterop

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy
import com.runninglane.dto.buddy.instance.InstanceStrategy
import com.runninglane.dto.buddy.javainterop.bytecode.DefaultByteCodeStrategy
import com.runninglane.dto.buddy.naming.DefaultNamingStrategy
import com.runninglane.dto.buddy.naming.NamingStrategy

private typealias KDtoBuddy = com.runninglane.dto.buddy.DtoBuddy

/**
 * DtoBuddy provides utilities for working with Data Transfer Objects (DTOs).
 * It can dynamically create concrete implementations of interfaces or abstract classes,
 * instantiate those implementations, and populate their properties.
 */
open class DtoBuddy {

    private val delegate: KDtoBuddy

    @Suppress("unused")
    open var namingStrategy: NamingStrategy
        get() = delegate.namingStrategy
        set(value) { delegate.namingStrategy = value }

    @Suppress("unused")
    open var byteCodeStrategy: ByteCodeStrategy
        get() = delegate.byteCodeStrategy
        set(value) { delegate.byteCodeStrategy = value }

    @Suppress("unused")
    open var instanceStrategy: InstanceStrategy
        get() = delegate.instanceStrategy
        set(value) { delegate.instanceStrategy = value }

    constructor(
        namingStrategy: NamingStrategy,
        byteCodeStrategy: ByteCodeStrategy,
        instanceStrategy: InstanceStrategy
    ) {
        delegate = KDtoBuddy(namingStrategy, byteCodeStrategy, instanceStrategy)
    }

    constructor(namingStrategy: NamingStrategy) : this(
        namingStrategy,
        DefaultByteCodeStrategy(),
        DefaultInstanceStrategy()
    )

    constructor(byteCodeStrategy: ByteCodeStrategy) : this(
        DefaultNamingStrategy(),
        byteCodeStrategy,
        DefaultInstanceStrategy()
    )

    constructor(instanceStrategy: InstanceStrategy) : this(
        DefaultNamingStrategy(),
        DefaultByteCodeStrategy(),
        instanceStrategy
    )

    constructor() : this(
        DefaultNamingStrategy(),
        DefaultByteCodeStrategy(),
        DefaultInstanceStrategy()
    )

    open fun implement(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?
    ) = delegate.implement(baseClass.kotlin, typeParams?.map { it.kotlin }).java

    open fun implement(baseClass: Class<*>) = implement(baseClass, null)

    open fun <DTO> create(concrete: Class<*>, params: Map<String, Any?>?): DTO =
        delegate.create(concrete.kotlin, params)

    open fun <DTO> create(concrete: Class<*>): DTO = create(concrete, null)

    open fun populate(dto: Any, params: Map<String, Any?>) {
        delegate.populate(dto, params)
    }
}