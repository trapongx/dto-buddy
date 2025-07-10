package com.runninglane.dto.buddy.test.cases.inner

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.reflect.full.isSubclassOf
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class InnerDtoInterfaceTest {
    private val dtoBuddy = DtoBuddy()

    @Test
    fun testImplement() {
        val concreteClass = dtoBuddy.implement(InnerDtoInterfaceEncloser.InnerDtoInterface::class)
        assertNotNull(concreteClass)
        assertTrue(concreteClass.isSubclassOf(InnerDtoInterfaceEncloser.InnerDtoInterface::class))
    }

}