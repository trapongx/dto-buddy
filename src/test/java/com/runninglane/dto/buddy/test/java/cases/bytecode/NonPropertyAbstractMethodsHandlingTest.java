package com.runninglane.dto.buddy.test.java.cases.bytecode;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.javainterop.bytecode.ByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.ThreeStepsByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.compile.CompileJavaByteCodeStrategyCompliment;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NonPropertyAbstractMethodsHandlingTest {
    public static abstract class TestBaseClass {
        protected String greeting = "Hello";

        public String getGreeting() {
            return greeting;
        }

        public void setGreeting(String greeting) {
            this.greeting = greeting;
        }

        public abstract String shout(String name);
    }

    static class TestByteCodeStrategyCompliment extends CompileJavaByteCodeStrategyCompliment {
        @Override
        public @NotNull TypeSpec.Builder handleNonPropertyAbstractMethods(
            @NotNull TypeSpec.Builder builder,
            @NotNull List<Method> methods,
            @Nullable Map<String, ? extends Class<?>> typeParamsMapByName
        ) {
            TypeSpec.Builder updatedBuilder = builder.addMethod(
                MethodSpec.methodBuilder("shout")
                    .addAnnotation(Override.class)
                    .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                    .addParameter(String.class, "name")
                    .returns(String.class)
                    .addCode("return getGreeting() + \" \" + name + \"!\";\n")
                    .build()
            );

            List<Method> unhandledMethods = methods.stream()
                .filter(method -> !method.getName().equals("shout"))
                .collect(Collectors.toList());

            return super.handleNonPropertyAbstractMethods(updatedBuilder, unhandledMethods, typeParamsMapByName);
        }
    }

    @Test
    public void testHandleNonPropertyAbstractMethods() {
        ByteCodeStrategy byteCodeStrategy = new ThreeStepsByteCodeStrategy<>(
            new TestByteCodeStrategyCompliment()
        );
        DtoBuddy dtoBuddy = new DtoBuddy(byteCodeStrategy);
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