package com.runninglane.dto.buddy.test.cases.notnull

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.test.*

class NonNullableTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun testInterfaceAndProperty() {
        val concreteClass = dtoBuddy.implement(DtoForCaseInterfaceAndProperty::class)
        val dto: DtoForCaseInterfaceAndProperty = dtoBuddy.create(
            concreteClass,
            mapOf("notNullable" to Dummy("Not Nullable Dummy"))
        )
        assertNotNull(dto)
        assertEquals("Not Nullable Dummy", dto.notNullable.name)
        assertNull(dto.nullable)
    }

    @Test
    fun testInterfaceAndGetter() {
        val concreteClass = dtoBuddy.implement(DtoForCaseInterfaceAndGetter::class)
        val dto: DtoForCaseInterfaceAndGetter = dtoBuddy.create(
            concreteClass,
            mapOf("notNullable" to Dummy("Not Nullable Dummy"))
        )
        assertNotNull(dto)
        assertEquals("Not Nullable Dummy", dto.getNotNullable().name)
        assertNull(dto.getNullable())
    }

    @Test
    fun testAbstractClassAndAbstractProperty() {
        val concreteClass = dtoBuddy.implement(DtoForCaseAbstractClassAndAbstractProperty::class)
        val dto: DtoForCaseAbstractClassAndAbstractProperty = dtoBuddy.create(
            concreteClass,
            mapOf("notNullable" to Dummy("Not Nullable Dummy"))
        )
        assertNotNull(dto)
        assertEquals("Not Nullable Dummy", dto.notNullable.name)
        assertNull(dto.nullable)
    }

    @Test
    fun testAbstractClassAndAbstractGetter() {
        val concreteClass = dtoBuddy.implement(DtoForCaseAbstractClassAndAbstractGetter::class)
        val dto: DtoForCaseAbstractClassAndAbstractGetter = dtoBuddy.create(
            concreteClass,
            mapOf("notNullable" to Dummy("Not Nullable Dummy"))
        )
        assertNotNull(dto)
        assertEquals("Not Nullable Dummy", dto.getNotNullable().name)
        assertNull(dto.getNullable())
    }
}