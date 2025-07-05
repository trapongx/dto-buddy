package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.DtoBuddy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;

import static com.runninglane.dto.buddy.test.java.cases.contract.BaseClasses.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The rule is the returned class must be a concrete class
 */
public class ReturnedClassContractTest {

    private final DtoBuddy dtoBuddy = new DtoBuddy();

    private void test(Class<?> baseClass, boolean expectBaseClassReturned) {
        try {
            Class<?> concreteClass = dtoBuddy.implement(baseClass);
            assertFalse(concreteClass.isInterface());
            assertFalse(Modifier.isAbstract(concreteClass.getModifiers()));
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
        }
    }

    @Test
    public void testInterface() {
        Arrays.asList(
            InterfaceWithNoMember.class,
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithDefaultGetter.class,
            InterfaceWithDefaultGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class,
            InterfaceWithAbstractPropertyAndDefaultGetter.class
        ).forEach(baseClass -> test(baseClass, false));
    }

    @Test
    public void testAbstractClass() {
        Arrays.asList(
            AbstractClassWithNoMember.class,
            AbstractClassWithAbstractGetter.class,
            AbstractClassWithAbstractGetterAndAbstractSetter.class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithoutField.class,
            AbstractClassWithConcreteGetterAndConcreteSetterWithField.class,
            AbstractClassWithAbstractImmutableProperty.class,
            AbstractClassWithAbstractMutableProperty.class,
            AbstractClassWithConcreteMutableProperty.class
        ).forEach(baseClass -> test(baseClass, false));
    }

    @Test
    public void testConcreteClass() {
        Arrays.asList(
            new AbstractMap.SimpleEntry<>(ConcreteClassWithNoMember.class, true),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithGetter.class, false),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithSetter.class, false),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithGetterAndSetterWithoutField.class, true),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithGetterAndSetterWithField.class, true),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithConcreteImmutableProperty.class, false),
            new AbstractMap.SimpleEntry<>(ConcreteClassWithConcreteMutableProperty.class, true)
        ).forEach(entry -> test(entry.getKey(), entry.getValue()));
    }

}