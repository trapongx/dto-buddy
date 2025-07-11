package com.runninglane.dto.buddy.javainterop

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy
import com.runninglane.dto.buddy.instance.InstanceStrategy
import com.runninglane.dto.buddy.javainterop.bytecode.DefaultByteCodeStrategy
import com.runninglane.dto.buddy.naming.DefaultNamingStrategy
import com.runninglane.dto.buddy.naming.NamingStrategy
import kotlin.reflect.KClass

private typealias KDtoBuddy = com.runninglane.dto.buddy.DtoBuddy

/**
 * DtoBuddy provides utilities for working with Data Transfer Objects (DTOs).
 * It can dynamically create concrete implementations of interfaces or abstract classes,
 * instantiate those implementations, and populate their properties.
 */
class DtoBuddy {

    private val delegate: KDtoBuddy

    var namingStrategy: NamingStrategy
        get() = delegate.namingStrategy
        set(value) { delegate.namingStrategy = value }

    var byteCodeStrategy: ByteCodeStrategy
        get() = delegate.byteCodeStrategy
        set(value) { delegate.byteCodeStrategy = value }

    var instanceStrategy: InstanceStrategy
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

    fun implement(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?
    ) = delegate.implement(baseClass.kotlin, typeParams?.map { it.kotlin }).java

    fun implement(baseClass: Class<*>) = implement(baseClass, null)

    fun <DTO> create(concrete: Class<*>, params: Map<String, Any?>?): DTO =
        delegate.create(concrete.kotlin, params)

    fun <DTO> create(concrete: Class<*>): DTO = create(concrete, null)

    fun populate(dto: Any, params: Map<String, Any?>) {
        delegate.populate(dto, params)
    }
}