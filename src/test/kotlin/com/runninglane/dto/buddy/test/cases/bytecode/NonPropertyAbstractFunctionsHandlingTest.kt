package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions
import kotlin.test.Test

class NonPropertyAbstractFunctionsHandlingTest {
    abstract class TestBaseClass {
        open var greeting: String = "Hello"
        abstract fun shout(name: String): String
    }

    class TestCodeGenerator : KotlinCodeGenerator() {
        override fun handleOtherAbstractMembers(
            builder: TypeSpec.Builder,
            properties: List<KProperty<*>>,
            functions: List<KFunction<*>>,
            typeParamsMapByName: Map<String, KClass<*>>?,
            dataCollector: Any?
        ): TypeSpec.Builder {
            val updatedBuilder = builder.addFunction(
                FunSpec.builder("shout")
                    .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                    .addParameter("name", String::class)
                    .returns(String::class)
                    .addStatement("return \"\${greeting} \$name!\"")
                    .build()
            )
            val unhandledFunctions = functions.filter { it.name != "shout" }
            return super.handleOtherAbstractMembers(updatedBuilder, properties, unhandledFunctions, typeParamsMapByName, dataCollector)
        }
    }

    @Test
    fun testHandleNonPropertyAbstractFunctions() {
        val byteCodeStrategy = CodeGenBasedByteCodeStrategy(TestCodeGenerator(), KotlinCodeCompiler())
        val dtoBuddy = DtoBuddy(byteCodeStrategy)
        val concreteClass = dtoBuddy.implement(TestBaseClass::class)
        val shoutFunction = concreteClass.functions.first { it.name == "shout" }
        assertFalse(shoutFunction.isAbstract)
        val dto = dtoBuddy.create<TestBaseClass>(concreteClass)
        dto.greeting = "Hello"
        assert(dto.shout("John") == "Hello John!")

        // Test with a different greeting
        dto.greeting = "Hi"
        assert(dto.shout("John") == "Hi John!")
    }
}