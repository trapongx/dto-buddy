package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import com.runninglane.dto.buddy.test.java.cases.contract.CompliantBaseClasses.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The rule is the returned class must be a concrete class
 */
class ReturnedClassContractTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy();

    private void test(Class<?> baseClass, boolean expectBaseClassReturned) {
        try {
            Class<?> concreteClass = dtoBuddy.implement(baseClass);
            assertFalse(concreteClass.isInterface());
            assertFalse(java.lang.reflect.Modifier.isAbstract(concreteClass.getModifiers()));
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
        }
    }

    @Test
    public void testInterface() {
        java.util.List.of(
            InterfaceWithNoMember.class,
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class
        ).forEach(baseClass -> test(baseClass, false));
    }

    @Test
    public void testAbstractClass() {
        java.util.List.of(
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
        java.util.Map.of(
            ConcreteClassWithNoMember.class, true,
            ConcreteClassWithGetter.class, false,
            ConcreteClassWithSetter.class, false,
            ConcreteClassWithGetterAndSetterWithoutField.class, true,
            ConcreteClassWithGetterAndSetterWithField.class, true
        ).forEach((baseClass, expectBaseClassReturned) ->
            test(baseClass, expectBaseClassReturned));
    }

}