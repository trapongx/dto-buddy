package com.runninglane.dto.buddy.test.cases.reservedwords

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class ReservedWordsTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun shouldFailWhenPropertyNameIsReservedWord() {
        assertDoesNotThrow {
            dtoBuddy.implement(DtoInterfaceWithReservedWords::class)
        }
    }
}