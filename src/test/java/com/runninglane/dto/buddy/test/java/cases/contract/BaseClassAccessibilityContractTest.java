package com.runninglane.dto.buddy.test.java.cases.contract;

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

    private final DtoBuddy dtoBuddy = new DtoBuddy();
    
    @Test
    public void shouldFailWhenClassIsPrivate() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            dtoBuddy.implementor(PrivateInterface.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsProtected() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            dtoBuddy.implementor(ProtectedInterface.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            dtoBuddy.implementor(FinalClassWithImmutableProperties.class).implement()
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow(() ->
            dtoBuddy.implementor(FinalClassWithNoProperties.class).implement()
        );
        assertDoesNotThrow(() ->
            dtoBuddy.implementor(FinalClassWithMutableProperties.class).implement()
        );
    }

    @Test
    public void shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow(() ->
            dtoBuddy.implementor(OpenClass.class).implement()
        );

        assertDoesNotThrow(() ->
            dtoBuddy.implementor(PublicInterface.class).implement()
        );

        assertDoesNotThrow(() -> {
            // Public internal
            dtoBuddy.implementor(InternalInterface.class).implement();
        });
    }

}