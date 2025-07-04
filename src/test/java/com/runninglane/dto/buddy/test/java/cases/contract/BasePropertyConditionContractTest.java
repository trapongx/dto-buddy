package com.runninglane.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BasePropertyConditionContractTest {

    public interface InterfaceWithAbstractGetter {
        String getName();
    }

    public interface InterfaceWithAbstractGetterAndAbstractSetter {
        String getName();

        void setName(String name);
    }

    public interface InterfaceWithDefaultGetter {
        default String getName() {
            return "John Doe";
        }
    }

    public interface InterfaceWithDefaultGetterAndAbstractSetter {
        default String getName() {
            return "John Doe";
        }

        void setName(String name);
    }

    public interface InterfaceWithAbstractProperty {
        String getName();
    }

    public interface InterfaceWithAbstractPropertyAndDefaultGetter {
        default String getName() {
            return "John Doe";
        }
    }

    public abstract static class AbstractClassWithAbstractGetter {
        public abstract String getName();
    }

    public abstract static class AbstractClassWithAbstractGetterAndAbstractSetter {
        public abstract String getName();

        public abstract void setName(String name);
    }

    public abstract static class AbstractClassWithConcreteGetterAndAbstractSetter {
        public String getName() {
            return "John";
        }

        public abstract void setName(String name);
    }

    public abstract static class AbstractClassWithAbstractGetterAndConcreteSetter {
        public abstract String getName();

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public abstract static class AbstractClassWithConcreteGetter {
        public String getName() {
            return "John Doe";
        }
    }

    public abstract static class AbstractClassWithConcreteSetter {
        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public abstract static class AbstractClassWithConcreteImmutableProperty {
        private final String name = "John Doe";

        public String getName() {
            return name;
        }
    }

    private void assertSucceed(Class<?> baseClass) {
        try {
            Class<?> concreteClass = DtoBuddy.implementor(baseClass).implement();
            Object dto = DtoBuddy.create(concreteClass, Map.of("name", "Test"));
            var getName = concreteClass.getMethod("getName");
            assertEquals("Test", getName.invoke(dto));
            var setName = concreteClass.getMethod("setName", String.class);
            setName.invoke(dto, "Test2");
            assertEquals("Test2", getName.invoke(dto));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
        }
    }

    @Test
    public void shouldSucceedWithInterfaceHavingGetterAndOptionallySetter() {
        Arrays.asList(
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithDefaultGetter.class,
            InterfaceWithDefaultGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class,
            InterfaceWithAbstractPropertyAndDefaultGetter.class
        ).forEach(this::assertSucceed);
    }

    @Test
    public void shouldSucceedWithAbstractClassHavingAbstractGetterAndOptionallyAbstractSetter() {
        Arrays.asList(
            AbstractClassWithAbstractGetter.class,
            AbstractClassWithAbstractGetterAndAbstractSetter.class
        ).forEach(this::assertSucceed);
    }

    @Test
    public void shouldSucceedWithoutPropertyImplementationWhenBaseClassIsAbstractClassHavingEitherConcreteGetterOrConcreteSetter() {
        {
            Class<?> concreteClass = DtoBuddy.implementor(AbstractClassWithConcreteGetter.class).implement();
            assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }

        {
            Class<?> concreteClass = DtoBuddy.implementor(AbstractClassWithConcreteSetter.class).implement();
            assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }

        {
            Class<?> concreteClass = DtoBuddy.implementor(AbstractClassWithConcreteImmutableProperty.class).implement();
            assertTrue(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("getName")));
            assertFalse(Arrays.stream(concreteClass.getMethods()).anyMatch(m -> m.getName().equals("setName")));
        }
    }

    @Test
    public void shouldFailWithAbstractClassHavingAPairOfDifferentConcretenessOfGetterAndSetter() {
        Arrays.asList(
            AbstractClassWithConcreteGetterAndAbstractSetter.class,
            AbstractClassWithAbstractGetterAndConcreteSetter.class
        ).forEach(baseClass -> {
            try {
                assertThrows(DtoBuddyBadInputException.class, () ->
                    DtoBuddy.implementor(baseClass).implement()
                );
            } catch (Throwable e) {
                throw new RuntimeException("Failed to test " + baseClass.getSimpleName(), e);
            }
        });
    }
}