package com.runninglane.dto.buddy.test.cases.simple

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.*

class SimpleDtoInterfaceTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun testImplement() {
        val concreteClass = dtoBuddy.implement(SimpleDtoInterface::class)
        assertNotNull(concreteClass)
        assertTrue(concreteClass.isSubclassOf(SimpleDtoInterface::class))
        assertEquals(setOf(SimpleDtoInterface::class, Any::class), concreteClass.supertypes.map { it.jvmErasure }.toSet())
    }

    @Test
    fun testCreate() {
        val params = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = dtoBuddy.implement(SimpleDtoInterface::class)
        val dto: SimpleDtoInterface = dtoBuddy.create(concreteClass, params)
        assertNotNull(dto)
        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateThenPopulate() {
        val params = mapOf(
            "email" to "john.doe@example.com"
        )

        val concreteClass = dtoBuddy.implement(SimpleDtoInterface::class)
        val dto: SimpleDtoInterface = dtoBuddy.create(
            concreteClass,
            mapOf("name" to "John Doe", "age" to 30, "email" to null)
        )
        assertNotNull(dto)
        dtoBuddy.populate(dto, params)
        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateWithNullValues() {
        val params = mapOf(
            "name" to "Jane Doe",
            "age" to 25,
            "email" to null
        )

        val concreteClass = dtoBuddy.implement(SimpleDtoInterface::class)
        val dto: SimpleDtoInterface = dtoBuddy.create(concreteClass, params)
        assertNotNull(dto)
        assertEquals("Jane Doe", dto.name)
        assertEquals(25, dto.age)
        assertNull(dto.email)
    }

    @Test
    fun testCreateThenPopulateWithMultipleValues() {
        val params = mapOf(
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = dtoBuddy.implement(SimpleDtoInterface::class)
        val dto: SimpleDtoInterface = dtoBuddy.create(
            concreteClass,
            mapOf("name" to "John Doe", "age" to 10, "email" to null)
        )
        assertNotNull(dto)
        dtoBuddy.populate(dto, params)
        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }
}