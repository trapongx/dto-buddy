package com.running.dto.buddy.test.java.cases.contract;

public interface BaseClasses {

    interface InterfaceWithNoMember {
    }

    interface InterfaceWithAbstractGetter {
        String getName();
    }

    interface InterfaceWithAbstractGetterAndAbstractSetter {
        String getName();

        void setName(String name);
    }

    interface InterfaceWithAbstractProperty {
        String getName();
    }

    abstract static class AbstractClassWithNoMember {
    }

    abstract static class AbstractClassWithAbstractGetter {
        public abstract String getName();
    }

    abstract static class AbstractClassWithAbstractGetterAndAbstractSetter {
        public abstract String getName();

        public abstract void setName(String name);
    }

    abstract static class AbstractClassWithConcreteGetterAndConcreteSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    abstract static class AbstractClassWithConcreteGetterAndConcreteSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    abstract static class AbstractClassWithAbstractImmutableProperty {
        public abstract String getName();
    }

    abstract static class AbstractClassWithAbstractMutableProperty {
        public abstract String getName();

        public abstract void setName(String name);
    }

    abstract static class AbstractClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class ConcreteClassWithNoMember {
    }

    static class ConcreteClassWithGetterAndSetterWithoutField {
        public String getName() {
            return "John Doe";
        }

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    static class ConcreteClassWithGetterAndSetterWithField {
        private String _name = "John Doe";

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    static class ConcreteClassWithConcreteMutableProperty {
        private String name = "John Doe";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
