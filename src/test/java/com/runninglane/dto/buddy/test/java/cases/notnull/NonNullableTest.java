package com.runninglane.dto.buddy.test.java.cases.notnull;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NonNullableTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy(new NamingStrategyWithCountUpSuffix());

    @Test
    public void testInterfaceAndGetter() throws NoSuchMethodException {
        Class<?> concreteClass = dtoBuddy.implement(DtoForCaseInterfaceAndGetter.class);
        DtoForCaseInterfaceAndGetter dto = dtoBuddy.create(
            concreteClass,
            Map.of("notNullable", new Dummy("Not Nullable Dummy"))
        );
        assertNotNull(dto);
        assertEquals("Not Nullable Dummy", dto.getNotNullable().getName());
        assertNull(dto.getNullable());
        assertTrue(
            Arrays.stream(concreteClass.getMethod("getNotNullable").getAnnotations())
                .anyMatch((annotation) -> annotation.toString().contains("NotNull"))
        );
        assertFalse(
            Arrays.stream(concreteClass.getMethod("getNullable").getAnnotations())
                .anyMatch((annotation) -> annotation.toString().contains("NotNull"))
        );
    }

    @Test
    public void testAbstractClassAndAbstractGetter() throws NoSuchMethodException {
        Class<?> concreteClass = dtoBuddy.implement(DtoForCaseAbstractClassAndAbstractGetter.class);
        DtoForCaseAbstractClassAndAbstractGetter dto = dtoBuddy.create(
            concreteClass,
            Map.of("notNullable", new Dummy("Not Nullable Dummy"))
        );
        assertNotNull(dto);
        assertEquals("Not Nullable Dummy", dto.getNotNullable().getName());
        assertNull(dto.getNullable());
        assertTrue(
            Arrays.stream(concreteClass.getMethod("getNotNullable").getAnnotations())
                .anyMatch((annotation) -> annotation.toString().contains("NotNull"))
        );
        assertFalse(
            Arrays.stream(concreteClass.getMethod("getNullable").getAnnotations())
                .anyMatch((annotation) -> annotation.toString().contains("NotNull"))
        );
    }
}