package com.running.dto.buddy.test.java.cases.contract;

interface BaseClasses {
    interface InterfaceWithNoMember {
    }

    interface InterfaceWithAbstractGetter {
        String getName();
    }

    interface InterfaceWithAbstractGetterAndAbstractSetter {
        String getName();

        void setName(String name);
    }

    interface InterfaceWithDefaultGetter {
        default String getName() {
            return "John Doe";
        }
    }

    interface InterfaceWithDefaultGetterAndAbstractSetter {
        default String getName() {
            return "John Doe";
        }

        void setName(String name);
    }

    interface InterfaceWithAbstractProperty {
        String getName();
    }

    interface InterfaceWithAbstractPropertyAndDefaultGetter {
        default String getName() {
            return "John Doe";
        }
    }

    abstract class AbstractClassWithNoMember {
    }

    abstract class AbstractClassWithAbstractGetter {
        public abstract String getName();
    }

    abstract class AbstractClassWithAbstractGetterAndAbstractSetter {
        public abstract String getName();

        public abstract void setName(String name);
    }

    abstract class AbstractClassWithConcreteGetterAndConcreteSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    abstract class AbstractClassWithConcreteGetterAndConcreteSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    abstract class AbstractClassWithAbstractImmutableProperty {
        public abstract String getName();
    }

    abstract class AbstractClassWithAbstractMutableProperty {
        public abstract String getName();

        public abstract void setName(String name);
    }

    abstract class AbstractClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class ConcreteClassWithNoMember {
    }

    class ConcreteClassWithGetter {
        public String getName() {
            return "John Doe";
        }
    }

    class ConcreteClassWithSetter {
        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    class ConcreteClassWithGetterAndSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    class ConcreteClassWithGetterAndSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    class ConcreteClassWithConcreteImmutableProperty {
        private final String name = "John Doe";

        public String getName() {
            return name;
        }
    }

    class ConcreteClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}