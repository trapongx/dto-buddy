package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

interface TestGetSetCapability {
    static void testGetSet(DtoBuddy dtoBuddy, Class<?> concreteClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");
        Object dto = dtoBuddy.create(concreteClass, params);

        Method getter = concreteClass.getMethod("getName");
        assertNotNull(getter);
        assertEquals(0, getter.getParameterCount());
        assertEquals("Test", getter.invoke(dto));

        Method setter = concreteClass.getMethod("setName", String.class);
        assertNotNull(setter);
        assertEquals(1, setter.getParameterCount());
        setter.invoke(dto, "Test2");
        assertEquals("Test2", getter.invoke(dto));
    }
}