package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyWrapper
import net.bytebuddy.dynamic.DynamicType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ByteCodeStrategyTest {
    abstract class TestDto

    annotation class TestAnnotation(val value: String)

    class TestByteBuddyWrapper : ByteBuddyWrapper() {
        override fun createDynamicType(
            baseClass: Class<*>,
            typeParams: List<Class<*>>?,
            packageName: String,
            className: String
        ): DynamicType.Builder<*> {
            return super.createDynamicType(baseClass, typeParams, packageName, className)
                .annotateType(TestAnnotation("test123"))
        }
    }

    @Test
    fun testAddSecondaryConstructor() {
        val dtoBuddy = DtoBuddy(ByteBuddyByteCodeStrategy(TestByteBuddyWrapper()))
        val concreteClass = dtoBuddy.implement(TestDto::class.java)
        val annotation = concreteClass.getAnnotation(TestAnnotation::class.java)
        assertNotNull(annotation)
        assertEquals("test123", annotation.value)
    }
}