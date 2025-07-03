package com.runninglane.dto.buddy.test.cases.abstracts

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SimpleAbstractDtoTest {
    companion object {
        private var nameSuffix: Int = 0
    }

    @Test
    fun testImplement() {
         val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java)

         assertNotNull(concreteClass)
         assertTrue(SimpleAbstractDto::class.java.isAssignableFrom(concreteClass))
    }

    @Test
    fun testCreate() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleAbstractDto = DtoBuddy.create(concreteClass, params)

        assertNotNull(dto)
        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateThenPopulate() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "email" to "john.doe@example.com"
        )

        val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleAbstractDto = DtoBuddy.create(
            concreteClass,
            mapOf("name" to "John Doe", "age" to 30, "email" to null)
        )

        assertNotNull(dto)
        DtoBuddy.populate(dto, params)
        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateWithNullValues() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "Jane Doe",
            "age" to 25,
            "email" to null
        )

        val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleAbstractDto = DtoBuddy.create(concreteClass, params)

        assertNotNull(dto)
        assertEquals("Jane Doe", dto.name)
        assertEquals(25, dto.age)
        assertEquals(null, dto.email)
    }

    @Test
    fun testCreateThenPopulateWithMultipleValues() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleAbstractDto = DtoBuddy.create(
            concreteClass,
            mapOf("name" to "John Doe", "age" to 10, "email" to null)
        )

        assertNotNull(dto)

        DtoBuddy.populate(dto, params)

        assertEquals("John Doe", dto.name)
        assertEquals(30, dto.age)
        assertEquals("john.doe@example.com", dto.email)
    }
}