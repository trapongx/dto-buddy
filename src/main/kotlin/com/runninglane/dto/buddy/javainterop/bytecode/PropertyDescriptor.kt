package com.runninglane.dto.buddy.javainterop.bytecode

import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

@OptIn(ExperimentalStdlibApi::class)
fun KPropertyDescriptor.java(): PropertyDescriptor = PropertyDescriptor(
    baseClass.java, name,
    type.javaType,
    isNullable,
    getter?.javaMethod, setter?.javaMethod,
    hasConcreteGetter, hasConcreteSetter
)

fun PropertyDescriptor.kotlin(): KPropertyDescriptor = KPropertyDescriptor(
    baseClass.kotlin, name,
    getter?.kotlinFunction?.returnType
        ?: setter?.kotlinFunction?.parameters?.get(1)?.type
        ?: error("Failed to determine property type for `$name` in ${baseClass.name}"),
    isNullable,
    null,
    getter?.kotlinFunction, setter?.kotlinFunction,
    hasConcreteGetter, hasConcreteSetter
)

/**
 * Helper class to track property metadata during analysis
 */
class PropertyDescriptor(
    val baseClass: Class<*>,
    val name: String,
    val type: Type,
    val isNullable: Boolean,
    val getter: Method?,
    val setter: Method?,
    val hasConcreteGetter: Boolean,
    val hasConcreteSetter: Boolean,
)
