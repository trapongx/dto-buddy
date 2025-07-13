package com.runninglane.dto.buddy.test.cases.collection

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.*

class CollectionTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun testImmutableList() {
        val concreteClass = dtoBuddy.implement(DtoWithImmutableList::class)
        assertNotNull(concreteClass)

        // Get the property's return type as a string to check the exact type
        val returnTypeStr = concreteClass.memberProperties
            .single { it.name == "items" }
            .returnType.toString()

        // Check that it's specifically List, not MutableList
        assertTrue(returnTypeStr.startsWith("kotlin.collections.List<"), 
            "Expected List but got $returnTypeStr")
        assertFalse(returnTypeStr.contains("MutableList<"), 
            "Got MutableList instead of List: $returnTypeStr")
    }

    @Test
    fun testMutableList() {
        val concreteClass = dtoBuddy.implement(DtoWithMutableList::class)
        assertNotNull(concreteClass)

        // Get the property's return type as a string to check the exact type
        val returnTypeStr = concreteClass.memberProperties
            .single { it.name == "items" }
            .returnType.toString()

        // Check that it's specifically MutableList, not just List
        assertTrue(returnTypeStr.contains("MutableList<"), 
            "Expected MutableList but got $returnTypeStr")
        assertFalse(returnTypeStr.startsWith("kotlin.collections.List<"), 
            "Got List instead of MutableList: $returnTypeStr")
    }
}