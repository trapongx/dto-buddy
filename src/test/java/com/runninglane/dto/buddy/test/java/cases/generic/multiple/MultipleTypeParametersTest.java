package com.runninglane.dto.buddy.test.java.cases.generic.multiple;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.javainterop.bytecode.ThreeStepsByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.compile.CompileJavaByteCodeStrategyCompliment;
import com.runninglane.dto.buddy.test.java.cases.generic.GenericTypeTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MultipleTypeParametersTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy(
        new ThreeStepsByteCodeStrategy<>(
            new CompileJavaByteCodeStrategyCompliment()
        )
    );

    private final GenericTypeTestHelper helper = new GenericTypeTestHelper();

    @Test
    public void testImplement() {
        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters.class,
            List.of(String.class, Integer.class, String.class, Long.class)
        );

        assertNotNull(concreteClass);
        assertTrue(DtoInterfaceWithMultipleTypeParameters.class.isAssignableFrom(concreteClass));
        assertEquals(String.class, helper.findMostSpecificMethodByName(concreteClass, "getSimple").getReturnType());
        assertEquals(List.class, helper.findMostSpecificMethodByName(concreteClass, "getList").getReturnType());
        assertEquals(Map.class, helper.findMostSpecificMethodByName(concreteClass, "getMap").getReturnType());
    }

    @Test
    public void testCreate() {
        // This test will fail until create() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("simple", "Simple String");
        params.put("list", List.of(1, 5, 9));
        Map<String, Long> mapParam = new HashMap<>();
        mapParam.put("key1", 1L);
        mapParam.put("key2", 2L);
        mapParam.put("key3", 3L);
        params.put("map", mapParam);

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters.class,
            List.of(String.class, Integer.class, String.class, Long.class)
        );
        DtoInterfaceWithMultipleTypeParameters<String, Integer, String, Long> dto = dtoBuddy.create(concreteClass, params);

        assertNotNull(dto);
        assertEquals("Simple String", dto.getSimple());
        assertEquals(List.of(1, 5, 9), dto.getList());
        assertEquals(mapParam, dto.getMap());
    }

    @Test
    public void testCreateThenPopulate() {
        // This test will fail until populate() is implemented
        Map<String, Object> params = new HashMap<>();
        Map<String, Long> mapParam = new HashMap<>();
        mapParam.put("key4", 4L);
        mapParam.put("key5", 5L);
        params.put("map", mapParam);

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters.class,
            List.of(String.class, Integer.class, String.class, Long.class)
        );

        Map<String, Object> createParams = new HashMap<>();
        createParams.put("simple", "Initial String");
        createParams.put("list", List.of(1, 2, 3));
        createParams.put("map", null);

        DtoInterfaceWithMultipleTypeParameters<String, Integer, String, Long> dto = dtoBuddy.create(concreteClass, createParams);

        assertNotNull(dto);
        dtoBuddy.populate(dto, params);
        assertEquals("Initial String", dto.getSimple());
        assertEquals(List.of(1, 2, 3), dto.getList());
        assertEquals(mapParam, dto.getMap());
    }

        @Test
    public void testCreateWithNullValues() {
            // This test will fail until create() is implemented
            Map<String, Object> params = new HashMap<>();
            params.put("simple", "Test String");
            params.put("list", List.of(10, 20, 30));
            params.put("map", null);

            Class<?> concreteClass = dtoBuddy.implement(
                DtoInterfaceWithMultipleTypeParameters.class,
                List.of(String.class, Integer.class, String.class, Long.class)
            );

            DtoInterfaceWithMultipleTypeParameters<String, Integer, String, Long> dto = dtoBuddy.create(concreteClass, params);

            assertNotNull(dto);
            assertEquals("Test String", dto.getSimple());
            assertEquals(List.of(10, 20, 30), dto.getList());
            assertNull(dto.getMap());
        }

        @Test
        public void testCreateThenPopulateWithMultipleValues() {
            Map<String, Object> params = new HashMap<>();
            params.put("simple", "Updated String");
            params.put("list", List.of(100, 200, 300));
            Map<String, Long> mapParam = new HashMap<>();
            mapParam.put("keyA", 10L);
            mapParam.put("keyB", 20L);
            params.put("map", mapParam);

            Class<?> concreteClass = dtoBuddy.implement(
                DtoInterfaceWithMultipleTypeParameters.class,
                List.of(String.class, Integer.class, String.class, Long.class)
            );

            Map<String, Object> createParams = new HashMap<>();
            createParams.put("simple", "Original String");
            createParams.put("list", List.of(1));
            Map<String, Long> initialMap = new HashMap<>();
            initialMap.put("key1", 1L);
            createParams.put("map", initialMap);

            DtoInterfaceWithMultipleTypeParameters<String, Integer, String, Long> dto = dtoBuddy.create(concreteClass, createParams);

            assertNotNull(dto);

            dtoBuddy.populate(dto, params);

            assertEquals("Updated String", dto.getSimple());
            assertEquals(List.of(100, 200, 300), dto.getList());
            assertEquals(mapParam, dto.getMap());
        }
    }