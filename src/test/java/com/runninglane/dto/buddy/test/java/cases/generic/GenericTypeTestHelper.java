package com.runninglane.dto.buddy.test.java.cases.generic;

import java.lang.reflect.Method;
import java.util.Arrays;

public class GenericTypeTestHelper {
    public Method findMostSpecificMethodByName(Class<?> clazz, String name) {
        return Arrays.stream(clazz.getMethods())
            .filter(method -> method.getName().equals(name))
            .min((m1, m2) -> {
                if (m1.getReturnType() != void.class && m2.getReturnType() != void.class) {
                    // If both methods return values, compare their return types
                    if (m1.getReturnType().isAssignableFrom(m2.getReturnType())) {
                        return 1; // m2 is more specific
                    } else if (m2.getReturnType().isAssignableFrom(m1.getReturnType())) {
                        return -1; // m1 is more specific
                    }
                } else if (m1.getParameterCount() > 0 && m2.getParameterCount() > 0) {
                    // If both methods have parameters, compare their first parameter types
                    Class<?> p1 = m1.getParameterTypes()[0];
                    Class<?> p2 = m2.getParameterTypes()[0];
                    if (p1.isAssignableFrom(p2)) {
                        return 1; // p2 is more specific
                    } else if (p2.isAssignableFrom(p1)) {
                        return -1; // p1 is more specific
                    }
                }
                return 0;
            })
            .orElseThrow(() -> new RuntimeException("Method not found: " + name));
    }
}
