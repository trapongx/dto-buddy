package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.test.cases.contract.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The rule is the returned class must be a concrete class
 */
public class GetterSetterBehaviorContractTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy();
    
    private void test(Class<?> baseClass, boolean expectBaseClassReturned, boolean testGetSet) {
        try {
            Class<?> concreteClass = dtoBuddy.implementor(baseClass).implement();
            assertFalse(concreteClass.isInterface());
            assertFalse(Modifier.isAbstract(concreteClass.getModifiers()));
            if (expectBaseClassReturned) {
                assertEquals(baseClass, concreteClass);
            }
            if (testGetSet) {
                Object dto = dtoBuddy.create(concreteClass, Map.of("name", "Test"));
                var getName = concreteClass.getMethod("getName");
                var setName = concreteClass.getMethod("setName", String.class);
                assertEquals("Test", getName.invoke(dto));
                setName.invoke(dto, "Test2");
                assertEquals("Test2", getName.invoke(dto));
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
        ).forEach(baseClass -> {
            boolean testGetSet = baseClass != InterfaceWithNoMember.class;
            test(baseClass, false, testGetSet);
        });
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
        ).forEach(baseClass -> {
            boolean testGetSet = !baseClass.getSimpleName().matches(".*(NoMember|Concrete).*");
            test(baseClass, false, testGetSet);
        });
    }

    @Test
    public void testConcreteClass() {
        Arrays.asList(
            ConcreteClassWithNoMember.class,
            ConcreteClassWithGetterAndSetterWithoutField.class,
            ConcreteClassWithGetterAndSetterWithField.class,
            ConcreteClassWithConcreteMutableProperty.class
        ).forEach(baseClass -> test(baseClass, true, false));
    }

}