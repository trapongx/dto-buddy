package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy
import net.bytebuddy.dynamic.DynamicType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ByteCodeStrategyTest {
    abstract class TestDto

    annotation class TestAnnotation(val value: String)

    class TestByteBuddyByteCodeStrategy : ByteBuddyByteCodeStrategy() {
        override fun defineClass(
            baseClass: Class<*>,
            typeParams: List<Class<*>>?,
            packageName: String,
            className: String
        ): DynamicType.Builder<*> {
            return super.defineClass(baseClass, typeParams, packageName, className)
                .annotateType(TestAnnotation("test123"))
        }
    }

    @Test
    fun testAddSecondaryConstructor() {
        val dtoBuddy = DtoBuddy(TestByteBuddyByteCodeStrategy())
        val concreteClass = dtoBuddy.implement(TestDto::class.java)
        val annotation = concreteClass.getAnnotation(TestAnnotation::class.java)
        assertNotNull(annotation)
        assertEquals("test123", annotation.value)
    }
}