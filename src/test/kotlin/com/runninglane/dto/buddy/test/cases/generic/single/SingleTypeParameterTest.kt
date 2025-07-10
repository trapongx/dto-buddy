package com.runninglane.dto.buddy.test.cases.generic.single

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SingleTypeParameterTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun testImplement() {
        run {
            val concreteClass = dtoBuddy.implement(
                DtoInterfaceWithSingleTypeParameter::class,
                listOf(String::class)
            )

            assertNotNull(concreteClass)
            assertTrue(concreteClass.isSubclassOf(DtoInterfaceWithSingleTypeParameter::class))
            assertEquals(String::class, concreteClass.memberProperties.single { it.name == "name" }.returnType.jvmErasure)
            assertEquals(Int::class, concreteClass.memberProperties.single { it.name == "age" }.returnType.jvmErasure)
            assertEquals(String::class, concreteClass.memberProperties.single { it.name == "email" }.returnType.jvmErasure)
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
            DtoInterfaceWithSingleTypeParameter::class,
            typeParams = listOf(String::class)
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
            DtoInterfaceWithSingleTypeParameter::class,
            typeParams = listOf(String::class)
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
            DtoInterfaceWithSingleTypeParameter::class,
            typeParams = listOf(String::class)
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
            DtoInterfaceWithSingleTypeParameter::class,
            typeParams = listOf(String::class)
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