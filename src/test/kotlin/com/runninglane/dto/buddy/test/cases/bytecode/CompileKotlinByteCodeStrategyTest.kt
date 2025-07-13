package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.ThreeStepsByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.compile.CompileKotlinByteCodeStrategyCompliment
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

    class TestCustomCompileKotlinByteCodeStrategyCompliment : CompileKotlinByteCodeStrategyCompliment() {

        override fun defineClass(
            baseClass: KClass<*>,
            typeParams: List<KClass<*>>?,
            packageName: String,
            className: String,
            dataCollector: Any?
        ): TypeSpec.Builder {
            val testAnnotationSpec = AnnotationSpec.builder(TestAnnotation::class)
                .addMember("value = %S", "test123")
                .build()
            return super.defineClass(baseClass, typeParams, packageName, className, dataCollector)
                .addAnnotation(testAnnotationSpec)
        }
    }

    @Test
    fun testAddCustomAnnotation() {
        val byteCodeStrategy = ThreeStepsByteCodeStrategy(TestCustomCompileKotlinByteCodeStrategyCompliment())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        val concreteClass = dtoBuddy.implement(TestDto::class)
        val annotation = concreteClass.findAnnotation<TestAnnotation>()
        assertNotNull(annotation)
        assertEquals("test123", annotation.value)
    }
}