package com.running.dto.buddy.test.java.cases.contract;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseClassAccessibilityContractTest {

    interface PrivateInterface {
    }

    protected interface ProtectedInterface {
    }

    public interface InternalInterface {
    }

    public interface PublicInterface {
    }

    public static final class FinalClassWithNoProperties {
    }

    public static final class FinalClassWithImmutableProperties {
        private final String name = "John";

        public String getName() {
            return name;
        }
    }

    public static final class FinalClassWithMutableProperties {
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
    public void shouldFailWhenClassIsPrivate() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(PrivateInterface.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsProtected() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(ProtectedInterface.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            DtoBuddy.implementor(FinalClassWithImmutableProperties.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow(() ->
            DtoBuddy.implementor(FinalClassWithNoProperties.class).implement()
        );
        assertDoesNotThrow(() ->
            DtoBuddy.implementor(FinalClassWithMutableProperties.class).implement()
        );
    }

    @Test
    public void shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow(() ->
            DtoBuddy.implementor(OpenClass.class).implement()
        );

        assertDoesNotThrow(() ->
            DtoBuddy.implementor(PublicInterface.class).implement()
        );

        assertDoesNotThrow(() -> {
            // Public internal
            DtoBuddy.implementor(InternalInterface.class).implement();
        });
    }

}