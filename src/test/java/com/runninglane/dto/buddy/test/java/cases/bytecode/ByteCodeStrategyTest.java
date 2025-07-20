package com.runninglane.dto.buddy.test.java.cases.bytecode;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.javainterop.bytecode.ByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeGenBasedByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeGenerator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ByteCodeStrategyTest {
    public static abstract class TestDto {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        String value();
    }

    static class TestByteBuddyByteCodeGenerator extends JavaCodeGenerator {
        @Override
        public @NotNull TypeSpec.Builder defineClass(
            @NotNull Class<?> baseClass,
            List<? extends Class<?>> typeParams,
            @NotNull String packageName,
            @NotNull String className,
            Object dataCollector
        ) {
            return super.defineClass(baseClass, typeParams, packageName, className, dataCollector)
                .addAnnotation(AnnotationSpec.builder(TestAnnotation.class)
                    .addMember("value", "$S", "test123")
                    .build());
        }
        
    }

    @Test
    public void testAddSecondaryConstructor() {
        ByteCodeStrategy byteCodeStrategy = new JavaCodeGenBasedByteCodeStrategy(new TestByteBuddyByteCodeGenerator());
        DtoBuddy dtoBuddy = new DtoBuddy(byteCodeStrategy);
        Class<?> concreteClass = dtoBuddy.implement(TestDto.class);
        TestAnnotation annotation = concreteClass.getAnnotation(TestAnnotation.class);
        assertNotNull(annotation);
        assertEquals("test123", annotation.value());
    }
}