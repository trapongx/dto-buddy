package com.runninglane.dto.buddy.test.cases.reservedwords

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.test.*

class ReservedWordsTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun shouldFailWhenPropertyNameIsReservedWord() {
        val exception = assertFailsWith<DtoBuddyBadInputException> {
            dtoBuddy.implement(DtoInterfaceWithReservedWords::class.java)
        }
        assertTrue(
            exception.message?.lowercase()?.contains("reserved word") ?: false,
            "Exception message should contain 'reserved word': ${exception.message}"
        )
    }
}