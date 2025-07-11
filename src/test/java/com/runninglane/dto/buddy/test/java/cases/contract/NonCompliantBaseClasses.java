package com.runninglane.dto.buddy.test.java.cases.contract;

public interface NonCompliantBaseClasses {
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

    interface InterfaceWithAbstractGetterAndDefaultSetter {
        String getName();

        default void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    abstract class AbstractClassWithConcreteGetterAndAbstractSetter {
        public String getName() {
            return "John";
        }

        public abstract void setName(String name);
    }

    abstract class AbstractClassWithAbstractGetterAndConcreteSetter {
        public abstract String getName();

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    abstract class AbstractClassWithConcreteGetter {
        public String getName() {
            return "John Doe";
        }
    }

    abstract class AbstractClassWithConcreteSetter {
        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

}