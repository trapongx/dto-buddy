package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static com.runninglane.dto.buddy.test.java.cases.contract.CompliantBaseClasses.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The rule is the returned class must be a concrete class
 */
public class GetterSetterBehaviorContractTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy();

    private void test(Class<?> baseClass, boolean expectBaseClassReturned, boolean testGetSet, boolean testGetSetInJavaStyle) {
        try {
            Class<?> concreteClass = dtoBuddy.implement(baseClass);
            assertFalse(concreteClass.isInterface());
            assertFalse(Modifier.isAbstract(concreteClass.getModifiers()));
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass);
            }
            if (testGetSet) {
                TestGetSetCapability.testGetSet(dtoBuddy, concreteClass);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
        }
    }

    @Test
    public void testInterface() {
        List<Class<?>> classes = Arrays.asList(
            InterfaceWithNoMember.class,
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class
        );

        for (Class<?> baseClass : classes) {
            boolean testGetSet = baseClass != InterfaceWithNoMember.class;
            boolean testGetSetInJavaStyle = testGetSet && !baseClass.getSimpleName().contains("Property");
            test(baseClass, false, testGetSet, testGetSetInJavaStyle);
        }
    }

    @Test
    public void testAbstractClass() {
        List<Class<?>> classes = Arrays.asList(
            AbstractClassWithNoMember.class,
            AbstractClassWithAbstractGetter.class,
            AbstractClassWithAbstractGetterAndAbstractSetter.class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithoutField.class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithField.class,
            AbstractClassWithAbstractImmutableProperty.class,
            AbstractClassWithAbstractMutableProperty.class,
            AbstractClassWithConcreteMutableProperty.class
        );

        for (Class<?> baseClass : classes) {
            boolean testGetSet = !baseClass.getSimpleName().matches(".*(NoMember|Concrete).*");
            boolean testGetSetInJavaStyle = testGetSet && !baseClass.getSimpleName().contains("Property");
            test(baseClass, false, testGetSet, testGetSetInJavaStyle);
        }
    }

    @Test
    public void testConcreteClass() {
        List<Class<?>> classes = Arrays.asList(
            ConcreteClassWithNoMember.class,
            ConcreteClassWithGetterAndSetterWithoutField.class,
            ConcreteClassWithGetterAndSetterWithField.class
        );

        for (Class<?> baseClass : classes) {
            test(baseClass, true, false, false);
        }
    }

}