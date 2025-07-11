package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.runninglane.dto.buddy.test.java.cases.contract.CompliantBaseClasses.*;
import static com.runninglane.dto.buddy.test.java.cases.contract.NonCompliantBaseClasses.*;

class BasePropertyConditionContractTest {
    private final DtoBuddy dtoBuddy = new DtoBuddy();

    private void assertSucceed (Class < ? > baseClass){
        try {
            Class<?> concreteClass = dtoBuddy.implement(baseClass);
            TestGetSetCapability.testGetSet(dtoBuddy, concreteClass);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
        }
    }

    @Test
    public void shouldSucceedWithInterfaceHavingAbstractGetterAndOptionallySetter () {
        List<Class<?>> classes = Arrays.asList(
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class
        );

        for (Class<?> baseClass : classes) {
            assertSucceed(baseClass);
        }
    }

    @Test
    public void shouldSucceedWithAbstractClassHavingAbstractGetterAndOptionallyAbstractSetter () {
        List<Class<?>> classes = Arrays.asList(
            AbstractClassWithAbstractGetter.class,
            AbstractClassWithAbstractGetterAndAbstractSetter.class
        );

        for (Class<?> baseClass : classes) {
            assertSucceed(baseClass);
        }
    }

    @Test
    public void shouldSucceedWithoutPropertyImplementationWhenBaseClassIsInterfaceOrAbstractClassHavingEitherConcreteGetterOrConcreteSetter
    () {
        {
            Class<?> concreteClass = dtoBuddy.implement(InterfaceWithDefaultGetter.class);
            Assertions.assertFalse(Arrays.stream(concreteClass.getDeclaredFields()).anyMatch(f -> f.getName().equals("name")));
            Assertions.assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            Assertions.assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }

        {
            Class<?> concreteClass = dtoBuddy.implement(AbstractClassWithConcreteGetter.class);
            Assertions.assertFalse(Arrays.stream(concreteClass.getDeclaredFields()).anyMatch(f -> f.getName().equals("name")));
            Assertions.assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            Assertions.assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }

        {
            Class<?> concreteClass = dtoBuddy.implement(AbstractClassWithConcreteSetter.class);
            Assertions.assertFalse(Arrays.stream(concreteClass.getDeclaredFields()).anyMatch(f -> f.getName().equals("name")));
            Assertions.assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            Assertions.assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }

    }

    @Test
    public void shouldFailWithAbstractClassHavingAPairOfDifferentConcretenessOfGetterAndSetter () {
        List<Class<?>> classes = Arrays.asList(
            InterfaceWithDefaultGetterAndAbstractSetter.class,
            InterfaceWithAbstractGetterAndDefaultSetter.class,
            AbstractClassWithConcreteGetterAndAbstractSetter.class,
            AbstractClassWithAbstractGetterAndConcreteSetter.class
        );

        for (Class<?> baseClass : classes) {
            try {
                Assertions.assertThrows(DtoBuddyBadInputException.class, () -> {
                    dtoBuddy.implement(baseClass);
                });
            } catch (Throwable e) {
                throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
            }
        }
    }

}