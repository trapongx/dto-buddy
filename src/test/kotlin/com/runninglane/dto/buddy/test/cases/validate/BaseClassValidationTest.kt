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
    class FinalClass
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
    fun shouldFailWhenClassIsFinal() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(FinalClass::class.java)
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