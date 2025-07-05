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
            dtoBuddy.implement(PrivateInterface.class)
        );
    }

    @Test
    public void shouldFailWhenClassIsProtected() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            dtoBuddy.implement(ProtectedInterface.class)
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows(DtoBuddyBadInputException.class, () ->
            dtoBuddy.implement(FinalClassWithImmutableProperties.class)
        );
    }

    @Test
    public void shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow(() ->
            dtoBuddy.implement(FinalClassWithNoProperties.class)
        );
        assertDoesNotThrow(() ->
            dtoBuddy.implement(FinalClassWithMutableProperties.class)
        );
    }

    @Test
    public void shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow(() ->
            dtoBuddy.implement(OpenClass.class)
        );

        assertDoesNotThrow(() ->
            dtoBuddy.implement(PublicInterface.class)
        );

        assertDoesNotThrow(() -> {
            // Public internal
            dtoBuddy.implement(InternalInterface.class);
        });
    }

}