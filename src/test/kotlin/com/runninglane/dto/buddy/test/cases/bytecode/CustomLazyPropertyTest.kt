package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomLazyPropertyTest {
    abstract class TestDto {
        var name: String = "John"
    }

    class Customizer : KotlinCodeGenerator() {

        override fun defineClass(
            baseClass: KClass<*>,
            typeParams: List<KClass<*>>?,
            packageName: String,
            className: String,
            dataCollector: Any?
        ): TypeSpec.Builder {
            return super.defineClass(baseClass, typeParams, packageName, className, dataCollector)
                .addProperty(
                    com.squareup.kotlinpoet.PropertySpec.builder("greeting", String::class)
                        .addModifiers(KModifier.PUBLIC)
                        .delegate("lazy { \"Hello \$name!\" }")
                        .build()
                )
        }
    }

    @Test
    fun testAddCustomAnnotation() {
        val byteCodeStrategy = CodeGenBasedByteCodeStrategy(Customizer(), KotlinCodeCompiler())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        val concreteClass = dtoBuddy.implement(TestDto::class)
        val dto = dtoBuddy.create<TestDto>(concreteClass)
        assertEquals("Hello John!", concreteClass.memberProperties.first { it.name == "greeting"}.call(dto))
    }
}