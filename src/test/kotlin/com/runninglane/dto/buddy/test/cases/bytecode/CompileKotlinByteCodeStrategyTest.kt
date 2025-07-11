package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.compile.CompileKotlinByteCodeStrategy
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CompileKotlinByteCodeStrategyTest {
    abstract class TestDto

    annotation class TestAnnotation(val value: String)

    class TestCustomCompileKotlinByteCodeStrategy : CompileKotlinByteCodeStrategy() {

        override fun defineClass(
            baseClass: KClass<*>,
            typeParams: List<KClass<*>>?,
            packageName: String,
            className: String
        ): TypeSpec.Builder {
            val testAnnotationSpec = AnnotationSpec.builder(TestAnnotation::class)
                .addMember("value = %S", "test123")
                .build()
            return super.defineClass(baseClass, typeParams, packageName, className)
                .addAnnotation(testAnnotationSpec)
        }
    }

    @Test
    fun testAddCustomAnnotation() {
        val dtoBuddy = DtoBuddy(TestCustomCompileKotlinByteCodeStrategy())
        val concreteClass = dtoBuddy.implement(TestDto::class)
        val annotation = concreteClass.findAnnotation<TestAnnotation>()
        assertNotNull(annotation)
        assertEquals("test123", annotation.value)
    }
}