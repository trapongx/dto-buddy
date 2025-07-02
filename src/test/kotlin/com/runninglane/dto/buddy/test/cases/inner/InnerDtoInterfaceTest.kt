package com.runninglane.dto.buddy.test.cases.inner

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class InnerDtoInterfaceTest {

    @Test
    fun testImplement() {
        val concreteClass = DtoBuddy.implement(InnerDtoInterfaceEncloser.InnerDtoInterface::class.java)
        assertNotNull(concreteClass)
        assertTrue(InnerDtoInterfaceEncloser.InnerDtoInterface::class.java.isAssignableFrom(concreteClass))
    }

}