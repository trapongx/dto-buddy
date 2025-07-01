package com.runninglane.dto.buddy.test.cases.inner

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class InnerDtoInterfaceTest {

    @Test
    fun testImplement() {
        // This test will fail until implement() is implemented
        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(InnerDtoInterfaceEncloser.InnerDtoInterface::class.java)
            assertNotNull(concreteClass)
        }

        // Once implemented, uncomment this test:
        // val implementedClass = DtoBuddy.implement(TestDto::class.java)
        // assertNotNull(implementedClass)
        // assertTrue(TestDto::class.java.isAssignableFrom(implementedClass))
    }

}