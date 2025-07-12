package com.runninglane.dto.buddy.test.java.cases.generic.single;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.javainterop.bytecode.ThreeStepsByteCodeStrategy;
import com.runninglane.dto.buddy.javainterop.bytecode.compile.CompileJavaByteCodeStrategyCompliment;
import com.runninglane.dto.buddy.test.java.cases.generic.GenericTypeTestHelper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SingleTypeParameterTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy(
        new ThreeStepsByteCodeStrategy<>(
            new CompileJavaByteCodeStrategyCompliment()
        )
    );

    private final GenericTypeTestHelper helper = new GenericTypeTestHelper();

    @Test
    public void testImplement() {
        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter.class,
            Collections.singletonList(String.class)
        );

        assertNotNull(concreteClass);
        assertTrue(DtoInterfaceWithSingleTypeParameter.class.isAssignableFrom(concreteClass));

        Method getName = helper.findMostSpecificMethodByName(concreteClass, "getName");
        assertEquals(String.class, getName.getReturnType());

        Method getAge = helper.findMostSpecificMethodByName(concreteClass, "getAge");
        assertEquals(Integer.class, getAge.getReturnType());

        Method getEmail = helper.findMostSpecificMethodByName(concreteClass, "getEmail");
        assertEquals(String.class, getEmail.getReturnType());
    }

    @Test
    public void testCreate() {
        // This test will fail until create() is implemented
        Map<String, Object> params = new HashMap<>();
        params.put("name", "John Doe");
        params.put("age", 30);
        params.put("email", "john.doe@example.com");

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter.class,
            Collections.singletonList(String.class)
        );
        DtoInterfaceWithSingleTypeParameter<String> dto = dtoBuddy.create(concreteClass, params);

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

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter.class,
            Collections.singletonList(String.class)
        );

        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 30);
        initialParams.put("email", null);

        DtoInterfaceWithSingleTypeParameter<String> dto = dtoBuddy.create(concreteClass, initialParams);

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

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter.class,
            Collections.singletonList(String.class)
        );
        DtoInterfaceWithSingleTypeParameter<String> dto = dtoBuddy.create(concreteClass, params);

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

        Class<?> concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter.class,
            Collections.singletonList(String.class)
        );

        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("name", "John Doe");
        initialParams.put("age", 10);
        initialParams.put("email", null);

        DtoInterfaceWithSingleTypeParameter<String> dto = dtoBuddy.create(concreteClass, initialParams);

        assertNotNull(dto);

        dtoBuddy.populate(dto, params);

        assertEquals("John Doe", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
    
}