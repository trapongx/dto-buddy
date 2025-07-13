package com.runninglane.dto.buddy.test.java.cases.nonpublic;

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.javainterop.bytecode.ThreeStepsByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.compile.CompileJavaByteCodeStrategyCompliment;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DtoWithProtectedGetterTest {

    public abstract static class TestDtoWithProtectedAbstractGetter {
        protected abstract String getName();
    }

    @Test
    public void shouldFailForTestDtoWithProtectedAbstractGetter() {
        DtoBuddy dtoBuddy = new DtoBuddy();
        assertThrows(DtoBuddyBadInputException.class, () -> dtoBuddy.implement(TestDtoWithProtectedAbstractGetter.class));
    }

    @Test
    public void shouldSucceedForTestDtoWithProtectedAbstractGetterWhenCustomizationApplied() {
        class TestByteCodeStrategyCompliment extends CompileJavaByteCodeStrategyCompliment {
            public @NotNull TypeSpec.Builder handleOtherAbstractMethods(
                @NotNull TypeSpec.Builder builder,
                @NotNull List<Method> methods,
                Map<String, ? extends Class<?>> typeParamsMapByName,
                Object dataCollector
            ) {
                TypeSpec.Builder updatedBuilder = builder.addMethod(
                    MethodSpec.methodBuilder("getName")
                        .addModifiers(Modifier.PROTECTED)
                        .addAnnotation(Override.class)
                        .returns(String.class)
                        .addCode("return \"Hello\";\n")
                        .build()
                );
                List<Method> unhandledMethods = methods.stream()
                    .filter(f -> !f.getName().equals("getName"))
                    .toList();
                return super.handleOtherAbstractMethods(updatedBuilder, unhandledMethods, typeParamsMapByName, dataCollector);
            }
        }

        ThreeStepsByteCodeStrategy<?> byteCodeStrategy = new ThreeStepsByteCodeStrategy<>(new TestByteCodeStrategyCompliment());
        DtoBuddy dtoBuddy = new DtoBuddy(byteCodeStrategy);
        dtoBuddy.implement(TestDtoWithProtectedAbstractGetter.class);
    }
}