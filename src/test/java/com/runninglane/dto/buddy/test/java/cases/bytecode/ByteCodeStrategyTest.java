package com.runninglane.dto.buddy.test.java.cases.bytecode;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyByteCodeStrategy;
import com.runninglane.dto.buddy.bytecode.bytebuddy.ByteBuddyWrapper;
import net.bytebuddy.dynamic.DynamicType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    static class TestByteBuddyWrapper extends ByteBuddyWrapper {
        @Override
        public @NotNull DynamicType.Builder<?> createDynamicType(
            @NotNull Class<?> baseClass,
            @Nullable List<? extends Class<?>> typeParams,
            @NotNull String packageName,
            @NotNull String className
        ) {
            return super.createDynamicType(baseClass, typeParams, packageName, className)
                .annotateType(new TestAnnotation() {
                    @Override
                    public String value() {
                        return "test123";
                    }

                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return TestAnnotation.class;
                    }
                });
        }
    }

    @Test
    public void testAddSecondaryConstructor() {
        DtoBuddy dtoBuddy = new DtoBuddy(new ByteBuddyByteCodeStrategy(new TestByteBuddyWrapper()));
        Class<?> concreteClass = dtoBuddy.implement(TestDto.class);
        TestAnnotation annotation = concreteClass.getAnnotation(TestAnnotation.class);
        assertNotNull(annotation);
        assertEquals("test123", annotation.value());
    }
}