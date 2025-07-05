package com.runninglane.dto.buddy.test.cases.generic.multiple

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MultipleTypeParametersTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun testImplement() {
        run {
            val concreteClass = dtoBuddy.implement(
                DtoInterfaceWithMultipleTypeParameters::class.java,
                listOf(String::class.java, Int::class.java, String::class.java, Long::class.java)
            )

            assertNotNull(concreteClass)
            assertTrue(DtoInterfaceWithMultipleTypeParameters::class.java.isAssignableFrom(concreteClass))
            assertEquals(String::class.java, concreteClass.methods.first { it.name == "getSimple" }.returnType)
            assertEquals(List::class.java, concreteClass.methods.first { it.name == "getList" }.returnType)
            assertEquals(Map::class.java, concreteClass.methods.first { it.name == "getMap" }.returnType)
        }
    }

    @Test
    fun testCreate() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "simple" to "Simple String",
            "list" to listOf(1, 5, 9),
            "map" to mapOf("key1" to 1L, "key2" to 2L, "key3" to 3L)
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters::class.java,
            typeParams = listOf(String::class.java, Int::class.java, String::class.java, Long::class.java)
        )
        val dto: DtoInterfaceWithMultipleTypeParameters<String, Int, String, Long> = dtoBuddy.create(concreteClass, params)

        assertNotNull(dto)
        assertEquals("Simple String", dto.simple)
        assertEquals(listOf(1, 5, 9), dto.list)
        assertEquals(mapOf("key1" to 1L, "key2" to 2L, "key3" to 3L), dto.map)
    }

    @Test
    fun testCreateThenPopulate() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "map" to mapOf("key4" to 4L, "key5" to 5L)
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters::class.java,
            typeParams = listOf(String::class.java, Int::class.java, String::class.java, Long::class.java)
        )
        val dto: DtoInterfaceWithMultipleTypeParameters<String, Int, String, Long> = dtoBuddy.create(
            concreteClass,
            mapOf("simple" to "Initial String", "list" to listOf(1, 2, 3), "map" to null)
        )

        assertNotNull(dto)
        dtoBuddy.populate(dto, params)
        assertEquals("Initial String", dto.simple)
        assertEquals(listOf(1, 2, 3), dto.list)
        assertEquals(mapOf("key4" to 4L, "key5" to 5L), dto.map)
    }

    @Test
    fun testCreateWithNullValues() {
        // This test will fail until create() is implemented
        val params = mapOf(
            "simple" to "Test String",
            "list" to listOf(10, 20, 30),
            "map" to null
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters::class.java,
            typeParams = listOf(String::class.java, Int::class.java, String::class.java, Long::class.java)
        )
        val dto: DtoInterfaceWithMultipleTypeParameters<String, Int, String, Long> = dtoBuddy.create(concreteClass, params)

        assertNotNull(dto)
        assertEquals("Test String", dto.simple)
        assertEquals(listOf(10, 20, 30), dto.list)
        assertEquals(null, dto.map)
    }

    @Test
    fun testCreateThenPopulateWithMultipleValues() {
        // This test will fail until populate() is implemented
        val params = mapOf(
            "simple" to "Updated String",
            "list" to listOf(100, 200, 300),
            "map" to mapOf("keyA" to 10L, "keyB" to 20L)
        )

        val concreteClass = dtoBuddy.implement(
            DtoInterfaceWithMultipleTypeParameters::class.java,
            typeParams = listOf(String::class.java, Int::class.java, String::class.java, Long::class.java)
        )
        val dto: DtoInterfaceWithMultipleTypeParameters<String, Int, String, Long> = dtoBuddy.create(
            concreteClass,
            mapOf("simple" to "Original String", "list" to listOf(1), "map" to mapOf("key1" to 1L))
        )

        assertNotNull(dto)

        dtoBuddy.populate(dto, params)

        assertEquals("Updated String", dto.simple)
        assertEquals(listOf(100, 200, 300), dto.list)
        assertEquals(mapOf("keyA" to 10L, "keyB" to 20L), dto.map)
    }
}