package com.runninglane.dto.buddy.test.cases.validate

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

private interface PrivateInterface

open class BaseClassValidationTest {

    protected interface ProtectedInterface
    internal interface InternalInterface
    interface PublicInterface
    class FinalClassWithNoProperties
    class FinalClassWithImmutableProperties {
        val name: String = "John"
    }
    class FinalClassWithMutableProperties {
        var name: String = "John"
    }
    open class OpenClass

    @Test
    fun shouldFailWhenClassIsProtected() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(ProtectedInterface::class.java)
        }
    }

    @Test
    fun shouldFailWhenClassIsPrivate() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(PrivateInterface::class.java)
        }
    }

    @Test
    fun shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(FinalClassWithImmutableProperties::class.java)
        }
    }

    @Test
    fun shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow {
            DtoBuddy.implement(FinalClassWithNoProperties::class.java)
        }
        assertDoesNotThrow {
            DtoBuddy.implement(FinalClassWithMutableProperties::class.java)
        }
    }

    @Test
    fun shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow {
            DtoBuddy.implement(OpenClass::class.java)
        }

        assertDoesNotThrow {
            DtoBuddy.implement(PublicInterface::class.java)
        }

        assertDoesNotThrow {
            // Public internal
            DtoBuddy.implement(InternalInterface::class.java)
        }
    }

}