package com.running.dto.buddy.test.java.cases.generic.single;

import com.runninglane.dto.buddy.DtoBuddy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SingleTypeParameterTest {
    private static int nameSuffix = 0;

    @Test
    public void testImplement() {
        Class<?> implementedClass = DtoBuddy.implementor(DtoInterfaceWithSingleTypeParameter.class)
            .withTypeParams(Collections.singletonList(String.class))
            .implement();

        assertNotNull(implementedClass);
        assertTrue(DtoInterfaceWithSingleTypeParameter.class.isAssignableFrom(implementedClass));
        assertEquals(String.class, Arrays.stream(implementedClass.getMethods())
            .filter(m -> m.getName().equals("getName"))
            .findFirst().get().getReturnType());
        assertEquals(Integer.class, Arrays.stream(implementedClass.getMethods())
            .filter(m -> m.getName().equals("getAge"))
            .findFirst().get().getReturnType());
        assertEquals(String.class, Arrays.stream(implementedClass.getMethods())
            .filter(m -> m.getName().equals("getEmail"))
            .findFirst().get().getReturnType());
    }

    @Test
    public void testCreate() {
        // This test will fail until create() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("name", "John Doe");
        params.put("age", 30);
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = DtoBuddy.implementor(DtoInterfaceWithSingleTypeParameter.class)
            .withTypeParams(Collections.singletonList(String.class))
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        DtoInterfaceWithSingleTypeParameter<String> dto = DtoBuddy.create(concreteClass, params);

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

        Class<?> concreteClass = DtoBuddy.implementor(DtoInterfaceWithSingleTypeParameter.class)
            .withTypeParams(Collections.singletonList(String.class))
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();

        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 30);
        initialParams.put("email", null);

        DtoInterfaceWithSingleTypeParameter<String> dto = DtoBuddy.create(concreteClass, initialParams);

        assertNotNull(dto);
        DtoBuddy.populate(dto, params);
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

        Class<?> concreteClass = DtoBuddy.implementor(DtoInterfaceWithSingleTypeParameter.class)
            .withTypeParams(Collections.singletonList(String.class))
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        DtoInterfaceWithSingleTypeParameter<String> dto = DtoBuddy.create(concreteClass, params);

        assertNotNull(dto);
        assertEquals("Jane Doe", dto.getName());
        assertEquals(25, dto.getAge());
        assertNull(dto.getEmail());
    }

    @Test
    public void testCreateThenPopulateWithMultipleValues() {
        Map<String, Object> params = new HashMap<>();
        params.put("age", 30);
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = DtoBuddy.implementor(DtoInterfaceWithSingleTypeParameter.class)
            .withTypeParams(Collections.singletonList(String.class))
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();

        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 10);
        initialParams.put("email", null);

        DtoInterfaceWithSingleTypeParameter<String> dto = DtoBuddy.create(concreteClass, initialParams);

        assertNotNull(dto);

        DtoBuddy.populate(dto, params);

        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
    
}