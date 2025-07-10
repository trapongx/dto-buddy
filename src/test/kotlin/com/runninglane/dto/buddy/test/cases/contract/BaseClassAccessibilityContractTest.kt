package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

open class BaseClassAccessibilityContractTest {

    private interface PrivateInterface

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

    private val dtoBuddy = DtoBuddy()

    @Test
    fun shouldFailWhenClassIsPrivate() {
        assertThrows<DtoBuddyBadInputException> {
            dtoBuddy.implement(PrivateInterface::class)
        }
    }

    @Test
    fun shouldFailWhenClassIsProtected() {
        assertThrows<DtoBuddyBadInputException> {
            dtoBuddy.implement(ProtectedInterface::class)
        }
    }

    @Test
    fun shouldFailWhenClassIsInternal() {
        assertThrows<DtoBuddyBadInputException> {
            dtoBuddy.implement(InternalInterface::class)
        }
    }

    @Test
    fun shouldFailWhenClassIsFinalAndIsNotCompleteAndMutable() {
        assertThrows<DtoBuddyBadInputException> {
            dtoBuddy.implement(FinalClassWithImmutableProperties::class)
        }
    }

    @Test
    fun shouldFailWhenClassIsFinalAndIsCompleteAndMutable() {
        assertDoesNotThrow {
            dtoBuddy.implement(FinalClassWithNoProperties::class)
        }
        assertDoesNotThrow {
            dtoBuddy.implement(FinalClassWithMutableProperties::class)
        }
    }

    @Test
    fun shouldSuccessWhenClassIsOpenAndPublic() {
        assertDoesNotThrow {
            dtoBuddy.implement(OpenClass::class)
        }

        assertDoesNotThrow {
            dtoBuddy.implement(PublicInterface::class)
        }
    }

}