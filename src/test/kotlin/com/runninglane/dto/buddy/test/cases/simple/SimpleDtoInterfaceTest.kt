package com.runninglane.dto.buddy.test.cases.simple

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SimpleDtoInterfaceTest {
    companion object {
        private var nameSuffix: Int = 0
    }

    @Test
    fun testImplement() {
        run {
            val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, nameSuffix = "${++nameSuffix}")
            assertNotNull(concreteClass)
            assertTrue(SimpleDtoInterface::class.java.isAssignableFrom(concreteClass))
            assertEquals("SimpleDtoInterface\$Dto$nameSuffix", concreteClass.simpleName)
            assertEquals(concreteClass.packageName, SimpleDtoInterface::class.java.packageName)
            assertEquals(listOf(SimpleDtoInterface::class.java), concreteClass.interfaces.toList())
            assertEquals(Any::class.java, concreteClass.superclass)
        }

        run {
            val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java)
            assertNotNull(concreteClass)
            assertTrue(SimpleDtoInterface::class.java.isAssignableFrom(concreteClass))
            assertEquals("SimpleDtoInterface\$Dto", concreteClass.simpleName)
            assertEquals(concreteClass.packageName, SimpleDtoInterface::class.java.packageName)
            assertEquals(listOf(SimpleDtoInterface::class.java), concreteClass.interfaces.toList())
            assertEquals(Any::class.java, concreteClass.superclass)
        }

        run {
            val customPackage = "com.example.test"
            val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, packageName = customPackage)
            assertNotNull(concreteClass)
            assertTrue(SimpleDtoInterface::class.java.isAssignableFrom(concreteClass))
            assertEquals("SimpleDtoInterface\$Dto", concreteClass.simpleName)
            assertEquals(customPackage, concreteClass.packageName)
            assertEquals(listOf(SimpleDtoInterface::class.java), concreteClass.interfaces.toList())
            assertEquals(Any::class.java, concreteClass.superclass)
        }

        run {
            val customPackage = "com.example.test"
            val concreteClass = DtoBuddy.implement(
                SimpleDtoInterface::class.java,
                packageName = customPackage,
                nameSuffix = "${++nameSuffix}"
            )
            assertNotNull(concreteClass)
            assertTrue(SimpleDtoInterface::class.java.isAssignableFrom(concreteClass))
            assertEquals("SimpleDtoInterface\$Dto$nameSuffix", concreteClass.simpleName)
            assertEquals(customPackage, concreteClass.packageName)
            assertEquals(listOf(SimpleDtoInterface::class.java), concreteClass.interfaces.toList())
            assertEquals(Any::class.java, concreteClass.superclass)
        }

    }

    @Test
    fun testCreate() {
        val params = mapOf(
            "name" to "John Doe",
            "age" to 30,
            "email" to "john.doe@example.com"
        )

        val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleDtoInterface = DtoBuddy.create(concreteClass, params)
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

        val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleDtoInterface = DtoBuddy.create(
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
        val params = mapOf(
            "name" to "Jane Doe",
            "age" to 25,
            "email" to null
        )

        val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleDtoInterface = DtoBuddy.create(concreteClass, params)
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

        val concreteClass = DtoBuddy.implement(SimpleDtoInterface::class.java, nameSuffix = "${++nameSuffix}")
        val dto: SimpleDtoInterface = DtoBuddy.create(
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