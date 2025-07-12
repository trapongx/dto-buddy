package com.runninglane.dto.buddy.test.cases.nonpublic

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.ThreeStepsByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.compile.CompileKotlinByteCodeStrategyCompliment
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
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
        class TestByteCodeStrategyCompliment : CompileKotlinByteCodeStrategyCompliment() {
            override fun handleOtherAbstractMembers(
                builder: TypeSpec.Builder,
                properties: List<KProperty<*>>,
                functions: List<KFunction<*>>,
                typeParamsMapByName: Map<String, KClass<*>>?
            ): TypeSpec.Builder {
                val updatedBuilder = builder.addProperty(
                    PropertySpec.builder("name", String::class)
                        .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                        .initializer("\"Hello\"")
                        .build()
                )
                val unhandledProperties = properties.filter { it.name != "name" }
                return super.handleOtherAbstractMembers(updatedBuilder, unhandledProperties, functions, typeParamsMapByName)
            }
        }

        val byteCodeStrategy = ThreeStepsByteCodeStrategy(TestByteCodeStrategyCompliment())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        dtoBuddy.implement(TestDtoWithProtectedAbstractImmutableProperty::class)
    }

    @Test
    fun shouldSucceedForTestDtoWithProtectedAbstractGetterWhenCustomizationApplied() {
        class TestByteCodeStrategyCompliment : CompileKotlinByteCodeStrategyCompliment() {
            override fun handleOtherAbstractMembers(
                builder: TypeSpec.Builder,
                properties: List<KProperty<*>>,
                functions: List<KFunction<*>>,
                typeParamsMapByName: Map<String, KClass<*>>?
            ): TypeSpec.Builder {
                val updatedBuilder = builder.addFunction(
                    FunSpec.builder("getName")
                        .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                        .returns(String::class)
                        .addStatement("return \"Hello\"")
                        .build()
                )
                val unhandledFunctions = functions.filter { it.name != "getName" }
                return super.handleOtherAbstractMembers(updatedBuilder, properties, unhandledFunctions, typeParamsMapByName)
            }
        }

        val byteCodeStrategy = ThreeStepsByteCodeStrategy(TestByteCodeStrategyCompliment())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        dtoBuddy.implement(TestDtoWithProtectedAbstractGetter::class)
    }
}