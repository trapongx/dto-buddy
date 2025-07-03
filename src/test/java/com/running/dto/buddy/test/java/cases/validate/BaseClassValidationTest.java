package com.running.dto.buddy.test.java.cases.validate;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

interface PrivateInterface {
}

public class BaseClassValidationTest {
    protected interface ProtectedInterface {
    }

    public interface PublicInterface {
    }

    class FinalClassWithNoProperties {
    }

    class FinalClassWithImmutableProperties {
        private final String name = "John";

        public String getName() {
            return name;
        }
    }

    class FinalClassWithMutableProperties {
        private String name = "John";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class OpenClass {
    }

    @Test
    public void shouldFailWhenClassIsProtected() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor().withBaseClass(ProtectedInterface.class).implement());
    }

    @Test
    public void shouldFailWhenClassIsPrivate() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor().withBaseClass(PrivateInterface.class).implement());
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(FinalClassWithImmutableProperties.class).implement());
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow(() ->
            DtoBuddy.implementor(FinalClassWithNoProperties.class).implement());
        assertDoesNotThrow(() ->
            DtoBuddy.implementor(FinalClassWithMutableProperties.class).implement());
    }

    @Test
    public void shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow(() ->
            DtoBuddy.implementor().withBaseClass(OpenClass.class).implement());

        assertDoesNotThrow(() ->
            DtoBuddy.implementor().withBaseClass(PublicInterface.class).implement());
    }

}