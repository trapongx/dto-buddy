package com.runninglane.dto.buddy.test.cases.nonpublic

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.Test

class DtoWithProtectedGetterTest {

    abstract class TestDtoWithProtectedAbstractImmutableProperty {
        protected abstract val name: String
    }

    abstract class TestDtoWithProtectedAbstractGetter {
        protected abstract fun getName(): String
    }

    @Test
    fun shouldFailForTestDtoWithProtectedAbstractImmutableProperty() {
        val dtoBuddy = DtoBuddy()
        assertThrows<DtoBuddyBadInputException> {
            dtoBuddy.implement(TestDtoWithProtectedAbstractImmutableProperty::class)
        }
    }

    @Test
    fun shouldFailForTestDtoWithProtectedAbstractGetter() {
        val dtoBuddy = DtoBuddy()
        assertThrows< DtoBuddyBadInputException> {
            dtoBuddy.implement(TestDtoWithProtectedAbstractGetter::class)
        }
    }

    @Test
    fun shouldSucceedForTestDtoWithProtectedConcreteImmutablePropertyWhenCustomizationApplied() {
        class TestCodeGenerator : KotlinCodeGenerator() {
            override fun handleOtherAbstractMembers(
                builder: TypeSpec.Builder,
                properties: List<KProperty<*>>,
                functions: List<KFunction<*>>,
                typeParamsMapByName: Map<String, KClass<*>>?,
                dataCollector: Any?
            ): TypeSpec.Builder {
                val updatedBuilder = builder.addProperty(
                    PropertySpec.builder("name", String::class)
                        .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                        .initializer("\"Hello\"")
                        .build()
                )
                val unhandledProperties = properties.filter { it.name != "name" }
                return super.handleOtherAbstractMembers(updatedBuilder, unhandledProperties, functions, typeParamsMapByName, dataCollector)
            }
        }

        val byteCodeStrategy = CodeGenBasedByteCodeStrategy(TestCodeGenerator(), KotlinCodeCompiler())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        dtoBuddy.implement(TestDtoWithProtectedAbstractImmutableProperty::class)
    }

    @Test
    fun shouldSucceedForTestDtoWithProtectedAbstractGetterWhenCustomizationApplied() {
        class TestCodeGenerator : KotlinCodeGenerator() {
            override fun handleOtherAbstractMembers(
                builder: TypeSpec.Builder,
                properties: List<KProperty<*>>,
                functions: List<KFunction<*>>,
                typeParamsMapByName: Map<String, KClass<*>>?,
                dataCollector: Any?
            ): TypeSpec.Builder {
                val updatedBuilder = builder.addFunction(
                    FunSpec.builder("getName")
                        .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                        .returns(String::class)
                        .addStatement("return \"Hello\"")
                        .build()
                )
                val unhandledFunctions = functions.filter { it.name != "getName" }
                return super.handleOtherAbstractMembers(updatedBuilder, properties, unhandledFunctions, typeParamsMapByName, dataCollector)
            }
        }

        val byteCodeStrategy = CodeGenBasedByteCodeStrategy(TestCodeGenerator(), KotlinCodeCompiler())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        dtoBuddy.implement(TestDtoWithProtectedAbstractGetter::class)
    }
}