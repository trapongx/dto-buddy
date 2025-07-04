package com.running.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.DtoBuddy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The rule is the returned class must be a concrete class
 */
public class ReturnedClassContractTest {

    public interface InterfaceWithNoMember {
    }

    public interface InterfaceWithAbstractGetter {
        String getName();
    }

    public interface InterfaceWithAbstractGetterAndAbstractSetter {
        String getName();

        void setName(String name);
    }

    public interface InterfaceWithAbstractProperty {
        String getName();
    }

    public abstract static class AbstractClassWithNoMember {
    }

    public abstract static class AbstractClassWithAbstractGetter {
        public abstract String getName();
    }

    public abstract static class AbstractClassWithAbstractGetterAndAbstractSetter {
        public abstract String getName();

        public abstract void setName(String name);
    }

    public abstract static class AbstractClassWithConcreteGetterAndConcreteSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public abstract static class AbstractClassWithConcreteGetterAndConcreteSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    public abstract static class AbstractClassWithAbstractImmutableProperty {
        public abstract String getName();
    }

    public abstract static class AbstractClassWithAbstractMutableProperty {
        public abstract String getName();

        public abstract void setName(String name);
    }

    public abstract static class AbstractClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ConcreteClassWithNoMember {
    }

    public static class ConcreteClassWithGetterAndSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public static class ConcreteClassWithGetterAndSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    public static class ConcreteClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testInterface() {
        Arrays.asList(
            InterfaceWithNoMember.class,
            InterfaceWithAbstractGetter.class,
            InterfaceWithAbstractGetterAndAbstractSetter.class,
            InterfaceWithAbstractProperty.class
        ).forEach(baseClass -> {
            Class<?> createdClass = DtoBuddy.implementor(baseClass).implement();
            assertFalse(createdClass.isInterface());
            assertFalse(Modifier.isAbstract(createdClass.getModifiers()));
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
            Class<?> createdClass = DtoBuddy.implementor(baseClass).implement();
            assertFalse(createdClass.isInterface());
            assertFalse(Modifier.isAbstract(createdClass.getModifiers()));
        });
    }

    @Test
    public void testConcreteClass() {
        Arrays.asList(
            new Object[]{ConcreteClassWithNoMember.class, true},
            new Object[]{ConcreteClassWithGetterAndSetterWithoutField.class, true},
            new Object[]{ConcreteClassWithGetterAndSetterWithField.class, true},
            new Object[]{ConcreteClassWithConcreteMutableProperty.class, true}
        ).forEach(pair -> {
            Class<?> baseClass = (Class<?>) ((Object[]) pair)[0];
            boolean selfComplete = (Boolean) ((Object[]) pair)[1];
            Class<?> createdClass = DtoBuddy.implementor(baseClass).implement();
            assertFalse(createdClass.isInterface());
            assertFalse(Modifier.isAbstract(createdClass.getModifiers()));
            if (selfComplete) {
                assertEquals(baseClass, createdClass);
            }
        });
    }

}