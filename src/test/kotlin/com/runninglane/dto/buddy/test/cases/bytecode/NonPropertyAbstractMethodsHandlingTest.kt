package com.runninglane.dto.buddy.test.cases.bytecode

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.Argument
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import net.bytebuddy.matcher.ElementMatchers
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.test.Test

class NonPropertyAbstractMethodsHandlingTest {
    abstract class TestBaseClass {
        open var greeting: String = "Hello"
        abstract fun shout(name: String): String
    }

    class ShoutDelegate {
        @RuntimeType
        fun shout(@Argument(0) name: String, @This instance: TestBaseClass): String {
            return "${instance.greeting} $name!"
        }
    }

    class TestByteCodeStrategy : ByteBuddyByteCodeStrategy() {
        override fun handleNonPropertyAbstractMethods(
            builder: DynamicType.Builder<*>,
            methods: List<Method>
        ): DynamicType.Builder<*> {
            val updatedBuilder = builder.method(ElementMatchers.named("shout"))
                .intercept(MethodDelegation.to(ShoutDelegate()))
                .annotateMethod(Override())
            val unhandledMethods = methods.filterNot { it.name == "shout" }
            return super.handleNonPropertyAbstractMethods(updatedBuilder, unhandledMethods)
        }
    }

    @Test
    fun testHandleNonPropertyAbstractMethods() {
        val dtoBuddy = DtoBuddy(TestByteCodeStrategy())
        val concreteClass = dtoBuddy.implement(TestBaseClass::class.java)
        val shoutMethod = concreteClass.methods.first { it.name == "shout" }
        assert(!Modifier.isAbstract(shoutMethod.modifiers))
        val dto = dtoBuddy.create<TestBaseClass>(concreteClass)
        dto.greeting = "Hello"
        assert(dto.shout("John") == "Hello John!")

        // Test with a different greeting
        dto.greeting = "Hi"
        assert(dto.shout("John") == "Hi John!")
    }
}