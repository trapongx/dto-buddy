package com.runninglane.dto.buddy.test.java.cases.simple;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDtoInterfaceTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy(new NamingStrategyWithCountUpSuffix());

    @Test
    public void testImplement() {
        Class<?> concreteClass = dtoBuddy.implement(SimpleDtoInterface.class);
        assertNotNull(concreteClass);
        assertTrue(SimpleDtoInterface.class.isAssignableFrom(concreteClass));
        assertEquals(List.of(SimpleDtoInterface.class), List.of(concreteClass.getInterfaces()));
        assertEquals(Object.class, concreteClass.getSuperclass());
    }

    @Test
    public void testCreate() {
        Map<String, Object> params = Map.of(
            "name", "John Doe",
            "age", 30,
            "email", "john.doe@example.com"
        );

        Class<?> concreteClass = dtoBuddy.implement(SimpleDtoInterface.class);
        SimpleDtoInterface dto = dtoBuddy.create(concreteClass, params);
        assertNotNull(dto);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    public void testCreateThenPopulate() {
        Map<String, Object> params = Map.of(
            "email", "john.doe@example.com"
        );

        Class<?> concreteClass = dtoBuddy.implement(SimpleDtoInterface.class);
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 30);
        initialParams.put("email", null);
        SimpleDtoInterface dto = dtoBuddy.create(concreteClass, initialParams);
        assertNotNull(dto);
        dtoBuddy.populate(dto, params);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    public void testCreateWithNullValues() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Jane Doe");
        params.put("age", 25);
        params.put("email", null);

        Class<?> concreteClass = dtoBuddy.implement(SimpleDtoInterface.class);
        SimpleDtoInterface dto = dtoBuddy.create(concreteClass, params);
        assertNotNull(dto);
        assertEquals("Jane Doe", dto.getName());
        assertEquals(25, dto.getAge());
        assertNull(dto.getEmail());
    }

    @Test
    public void testCreateThenPopulateWithMultipleValues() {
        Map<String, Object> params = Map.of(
            "age", 30,
            "email", "john.doe@example.com"
        );

        Class<?> concreteClass = dtoBuddy.implement(SimpleDtoInterface.class);
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 10);
        initialParams.put("email", null);
        SimpleDtoInterface dto = dtoBuddy.create(concreteClass, initialParams);
        assertNotNull(dto);
        dtoBuddy.populate(dto, params);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
}