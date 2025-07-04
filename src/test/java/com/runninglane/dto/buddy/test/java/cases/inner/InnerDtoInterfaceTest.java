package com.runninglane.dto.buddy.test.java.cases.inner;

import com.runninglane.dto.buddy.DtoBuddy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InnerDtoInterfaceTest {

    @Test
    public void testImplement()
    {
        Class<?> concreteClass = DtoBuddy.implementor()
            .withBaseClass(InnerDtoInterfaceEncloser.InnerDtoInterface.class)
            .implement();
        assertNotNull(concreteClass);
        assertTrue(InnerDtoInterfaceEncloser.InnerDtoInterface.class. isAssignableFrom (concreteClass));
    }

}