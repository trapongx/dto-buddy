package com.runninglane.dto.buddy.test.java.cases.bytecode;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NonPropertyAbstractMethodsHandlingTest {
    public abstract static class TestBaseClass {
        protected String greeting = "Hello";

        public String getGreeting() {
            return greeting;
        }

        public void setGreeting(String greeting) {
            this.greeting = greeting;
        }

        abstract String shout(String name);
    }

    public static class ShoutDelegate {
        @RuntimeType
        public String shout(@Argument(0) String name, @This TestBaseClass instance) {
            return instance.getGreeting() + " " + name + "!";
        }
    }

    static class TestByteCodeStrategy extends ByteBuddyByteCodeStrategy {
        @Override
        public DynamicType.Builder<?> handleNonPropertyAbstractMethods(
            DynamicType.Builder<?> builder,
            List<Method> methods
        ) {
            DynamicType.Builder<?> updatedBuilder = builder.method(ElementMatchers.named("shout"))
                .intercept(MethodDelegation.to(new ShoutDelegate()))
                .annotateMethod(new Override() {
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return Override.class;
                    }
                });

            List<Method> unhandledMethods = methods.stream()
                .filter(method -> !method.getName().equals("shout"))
                .collect(Collectors.toList());

            return super.handleNonPropertyAbstractMethods(updatedBuilder, unhandledMethods);
        }
    }

    // TODO uncomment this test method when we can find the fix
    //@Test
    public void testHandleNonPropertyAbstractMethods() {
        DtoBuddy dtoBuddy = new DtoBuddy(new TestByteCodeStrategy());
        Class<?> concreteClass = dtoBuddy.implement(TestBaseClass.class, null);
        Method shoutMethod = Arrays.stream(concreteClass.getMethods())
            .filter(method -> method.getName().equals("shout"))
            .findFirst()
            .orElseThrow();
        assert !Modifier.isAbstract(shoutMethod.getModifiers());
        TestBaseClass dto = dtoBuddy.create(concreteClass);
        dto.setGreeting("Hello");
        assert "Hello John!".equals(dto.shout("John"));

        // Test with a different greeting
        dto.setGreeting("Hi");
        assert "Hi John!".equals(dto.shout("John"));
    }
}