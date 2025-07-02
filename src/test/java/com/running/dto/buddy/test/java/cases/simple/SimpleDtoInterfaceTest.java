package com.running.dto.buddy.test.java.cases.simple;

import com.runninglane.dto.buddy.DtoBuddy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDtoInterfaceTest {
    private static int nameSuffix = 0;

    @Test
    public void testImplement() {
        {
            Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
                .withNameSuffix(String.valueOf(++nameSuffix))
                .implement();
            assertNotNull(concreteClass);
            assertTrue(SimpleDtoInterface.class.isAssignableFrom(concreteClass));
            assertEquals("SimpleDtoInterface$Dto" + nameSuffix, concreteClass.getSimpleName());
            assertEquals(concreteClass.getPackageName(), SimpleDtoInterface.class.getPackageName());
            assertEquals(List.of(SimpleDtoInterface.class), Arrays.asList(concreteClass.getInterfaces()));
            assertEquals(Object.class, concreteClass.getSuperclass());
        }

        {
            Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
                .implement();
            assertNotNull(concreteClass);
            assertTrue(SimpleDtoInterface.class.isAssignableFrom(concreteClass));
            assertEquals("SimpleDtoInterface$Dto", concreteClass.getSimpleName());
            assertEquals(concreteClass.getPackageName(), SimpleDtoInterface.class.getPackageName());
            assertEquals(List.of(SimpleDtoInterface.class), Arrays.asList(concreteClass.getInterfaces()));
            assertEquals(Object.class, concreteClass.getSuperclass());
        }

        {
            String customPackage = "com.example.test";
            Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
                .withPackageName(customPackage)
                .implement();
            assertNotNull(concreteClass);
            assertTrue(SimpleDtoInterface.class.isAssignableFrom(concreteClass));
            assertEquals("SimpleDtoInterface$Dto", concreteClass.getSimpleName());
            assertEquals(customPackage, concreteClass.getPackageName());
            assertEquals(List.of(SimpleDtoInterface.class), Arrays.asList(concreteClass.getInterfaces()));
            assertEquals(Object.class, concreteClass.getSuperclass());
        }

        {
            String customPackage = "com.example.test";
            Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
                .withPackageName(customPackage)
                .withNameSuffix(String.valueOf(++nameSuffix))
                .implement();
            assertNotNull(concreteClass);
            assertTrue(SimpleDtoInterface.class.isAssignableFrom(concreteClass));
            assertEquals("SimpleDtoInterface$Dto" + nameSuffix, concreteClass.getSimpleName());
            assertEquals(customPackage, concreteClass.getPackageName());
            assertEquals(List.of(SimpleDtoInterface.class), Arrays.asList(concreteClass.getInterfaces()));
            assertEquals(Object.class, concreteClass.getSuperclass());
        }

        assertThrows(IllegalArgumentException.class, () -> DtoBuddy.implementor().implement());
    }

    @Test
    public void testCreate() {
        Map<String, Object> params = Map.of(
            "name", "John Doe",
            "age", 30,
            "email", "john.doe@example.com"
        );

        Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        SimpleDtoInterface dto = DtoBuddy.create(concreteClass, params);
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

        Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 30);
        initialParams.put("email", null);
        SimpleDtoInterface dto = DtoBuddy.create(concreteClass, initialParams);
        assertNotNull(dto);
        DtoBuddy.populate(dto, params);
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

        Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        SimpleDtoInterface dto = DtoBuddy.create(concreteClass, params);
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

        Class<?> concreteClass = DtoBuddy.implementor(SimpleDtoInterface.class)
            .withNameSuffix(String.valueOf(++nameSuffix))
            .implement();
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 10);
        initialParams.put("email", null);
        SimpleDtoInterface dto = DtoBuddy.create(concreteClass, initialParams);
        assertNotNull(dto);
        DtoBuddy.populate(dto, params);
        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
}