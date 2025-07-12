package com.runninglane.dto.buddy.test.java.cases.notnull;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

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
        assertTrue(concreteClass.getMethod("getNotNullable").isAnnotationPresent(NotNull.class));
        assertTrue(concreteClass.getMethod("getNullable").isAnnotationPresent(Nullable.class));
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
        assertTrue(concreteClass.getMethod("getNotNullable").isAnnotationPresent(NotNull.class));
        assertTrue(concreteClass.getMethod("getNullable").isAnnotationPresent(Nullable.class));
    }
}