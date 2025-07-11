package com.runninglane.dto.buddy.test.java.cases.abstracts;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleAbstractDtoTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy(new NamingStrategyWithCountUpSuffix());

    @Test
    public void testImplement() {
        Class<?> concreteClass = dtoBuddy.implement(SimpleAbstractDto.class);

        assertNotNull(concreteClass);
        assertTrue(SimpleAbstractDto.class.isAssignableFrom(concreteClass));
    }

    @Test
    public void testCreate() {
        // This test will fail until create() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("name", "John Doe");
        params.put("age", 30);
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = dtoBuddy.implement(SimpleAbstractDto.class);
        SimpleAbstractDto dto = dtoBuddy.create(concreteClass, params);

        assertNotNull(dto);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    public void testCreateThenPopulate() {
        // This test will fail until populate() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = dtoBuddy.implement(SimpleAbstractDto.class);
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 30);
        initialParams.put("email", null);
        SimpleAbstractDto dto = dtoBuddy.create(concreteClass, initialParams);

        assertNotNull(dto);
        dtoBuddy.populate(dto, params);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    public void testCreateWithNullValues() {
        // This test will fail until create() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Jane Doe");
        params.put("age", 25);
        params.put("email", null);

        Class<?> concreteClass = dtoBuddy.implement(SimpleAbstractDto.class);
        SimpleAbstractDto dto = dtoBuddy.create(concreteClass, params);

        assertNotNull(dto);
        assertEquals("Jane Doe", dto.getName());
        assertEquals(25, dto.getAge());
        assertNull(dto.getEmail());
    }

    @Test
    public void testCreateThenPopulateWithMultipleValues() {
        // This test will fail until populate() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("age", 30);
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = dtoBuddy.implement(SimpleAbstractDto.class);
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 10);
        initialParams.put("email", null);
        SimpleAbstractDto dto = dtoBuddy.create(concreteClass, initialParams);

        assertNotNull(dto);

        dtoBuddy.populate(dto, params);

        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
}