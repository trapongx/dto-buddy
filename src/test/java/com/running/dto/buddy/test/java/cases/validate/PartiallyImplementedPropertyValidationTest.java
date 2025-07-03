package com.running.dto.buddy.test.java.cases.validate;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PartiallyImplementedPropertyValidationTest {

    abstract class AbstractClassWithConcreteGetterAndAbstractSetter {
        public String getName() {
            return "John";
        }

        public abstract void setName(String name);
    }

    abstract static
    class AbstractClassWithAbstractGetterAndConcreteSetter {
        public abstract String getName();

        public void setName(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Test
    public void shouldFailWhenGetterIsConcreteAndSetterIsAbstract() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(AbstractClassWithConcreteGetterAndAbstractSetter.class).implement()
        );
    }

    @Test
    public void shouldFailWhenGetterIsAbstractAndSetterIsConcrete() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(AbstractClassWithAbstractGetterAndConcreteSetter.class).implement()
        );
    }
}