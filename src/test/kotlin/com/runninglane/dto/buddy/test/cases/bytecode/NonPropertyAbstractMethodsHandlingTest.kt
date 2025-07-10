package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.k2jvm.EmbeddedCompilerByteCodeStrategy
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.junit.jupiter.api.Assertions.assertFalse
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.test.Test

class NonPropertyAbstractMethodsHandlingTest {
    abstract class TestBaseClass {
        open var greeting: String = "Hello"
        abstract fun shout(name: String): String
    }

    class TestByteCodeStrategy : EmbeddedCompilerByteCodeStrategy() {
        override fun handleNonPropertyAbstractFunctions(
            builder: TypeSpec.Builder,
            functions: List<KFunction<*>>
        ): TypeSpec.Builder {
            val updatedBuilder = builder.addFunction(
                FunSpec.builder("shout")
                    .addModifiers(com.squareup.kotlinpoet.KModifier.OVERRIDE)
                    .addParameter("name", String::class)
                    .returns(String::class)
                    .addStatement("return \"\${greeting} \$name!\"")
                    .build()
            )
            val unhandledMethods = functions.filter { it.name != "shout" }
            return super.handleNonPropertyAbstractFunctions(updatedBuilder, unhandledMethods)
        }
    }

    @Test
    fun testHandleNonPropertyAbstractMethods() {
        val dtoBuddy = DtoBuddy(TestByteCodeStrategy())
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