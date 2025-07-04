package com.runninglane.dto.buddy.test.cases.generic.single

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SingleTypeParameterTest {
    companion object {
        private var nameSuffix: Int = 0
    }

    private val dtoBuddy = DtoBuddy()

    @Test
    fun testImplement() {
        run {
            val concreteClass = dtoBuddy.implement(
                DtoInterfaceWithSingleTypeParameter::class.java,
                listOf(String::class.java)
            )

            assertNotNull(concreteClass)
            assertTrue(DtoInterfaceWithSingleTypeParameter::class.java.isAssignableFrom(concreteClass))
            assertEquals(String::class.java, concreteClass.methods.first { it.name == "getName" }.returnType)
            assertEquals(Int::class.java, concreteClass.methods.first { it.name == "getAge" }.returnType)
            assertEquals(String::class.java, concreteClass.methods.first { it.name == "getEmail" }.returnType)
        }
    }

    @Test
    fun testCreate() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter::class.java,
            typeParams = listOf(String::class.java),
            nameSuffix = "${++nameSuffix}"
        )
        val dto: DtoInterfaceWithSingleTypeParameter<String> = dtoBuddy.create(concreteClass, params)

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

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter::class.java,
            typeParams = listOf(String::class.java),
            nameSuffix = "${++nameSuffix}"
        )
        val dto: DtoInterfaceWithSingleTypeParameter<String> = dtoBuddy.create(
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
        // This test will fail until create() is implemented
        val params = mapOf(
            "name" to "Jane Doe",
            "age" to 25,
            "email" to null
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter::class.java,
            typeParams = listOf(String::class.java),
            nameSuffix = "${++nameSuffix}"
        )
        val dto: DtoInterfaceWithSingleTypeParameter<String> = dtoBuddy.create(concreteClass, params)

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

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithSingleTypeParameter::class.java,
            typeParams = listOf(String::class.java),
            nameSuffix = "${++nameSuffix}"
        )
        val dto: DtoInterfaceWithSingleTypeParameter<String> = dtoBuddy.create(
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