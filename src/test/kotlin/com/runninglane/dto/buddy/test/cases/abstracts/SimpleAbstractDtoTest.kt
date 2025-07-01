package com.runninglane.dto.buddy.test.cases.abstracts

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class SimpleAbstractDtoTest {
    companion object {
        private var nameSuffix: Int = 0
    }

    @Test
    fun testImplement() {
        // This test will fail until implement() is implemented
        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
            assertNotNull(concreteClass)
        }

        // Once implemented, uncomment this test:
        // val implementedClass = DtoBuddy.implement(TestDto::class.java)
        // assertNotNull(implementedClass)
        // assertTrue(TestDto::class.java.isAssignableFrom(implementedClass))
    }

    @Test
    fun testCreate() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
            val dto: SimpleAbstractDto = DtoBuddy.create(concreteClass, params)
            assertNotNull(dto)
        }

        // Once implemented, uncomment this test:
        // val dto: TestDto = DtoBuddy.create(ConcreteDto::class.java, params)
        // assertEquals("John Doe", dto.name)
        // assertEquals(30, dto.age)
        // assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateThenPopulate() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "email" to "john.doe@example.com"
        )

        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
            val dto: SimpleAbstractDto = DtoBuddy.create(
                concreteClass,
                mapOf("name" to "John Doe", "age" to 30, "email" to null)
            )
            assertNotNull(dto)
            DtoBuddy.populate(dto, params)
        }

        // Once implemented, uncomment this test:
        // val dto = ...
        // DtoBuddy.populate(dto, params)
        // assertEquals("John Doe", dto.name)
        // assertEquals(30, dto.age)
        // assertEquals("john.doe@example.com", dto.email)
    }

    @Test
    fun testCreateWithNullValues() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "Jane Doe",
            "age" to 25,
            "email" to null
        )

        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
            val dto: SimpleAbstractDto = DtoBuddy.create(concreteClass, params)
            assertNotNull(dto)
        }

        // Once implemented, uncomment this test:
        // val dto: TestDto = DtoBuddy.create(ConcreteDto::class.java, params)
        // assertEquals("Jane Doe", dto.name)
        // assertEquals(25, dto.age)
        // assertEquals(null, dto.email)
    }

    @Test
    fun testCreateThenPopulateWithMultipleValues() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        assertFailsWith<NotImplementedError> {
            val concreteClass = DtoBuddy.implement(SimpleAbstractDto::class.java, nameSuffix = "${++nameSuffix}")
            val dto: SimpleAbstractDto = DtoBuddy.create(
                concreteClass,
                mapOf("name" to "John Doe", "age" to 10, "email" to null)
            )
            assertNotNull(dto)
            DtoBuddy.populate(dto, params)
        }

        // Once implemented, uncomment this test:
        // val dto = ...
        // DtoBuddy.populate(dto, params)
        // assertEquals("John Doe", dto.name)
        // assertEquals(30, dto.age)
        // assertEquals("john.doe@example.com", dto.email)
    }
}