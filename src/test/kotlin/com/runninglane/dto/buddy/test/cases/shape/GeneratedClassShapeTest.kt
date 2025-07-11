package com.runninglane.dto.buddy.test.cases.shape

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.test.NamingStrategyWithCountUpSuffix
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GeneratedClassShapeTest {
    private val dtoBuddy = DtoBuddy(NamingStrategyWithCountUpSuffix())

    @Test
    fun generatedClassShouldHaveCorrectKotlinShape() {
        val concreteClass = dtoBuddy.implement(TestDtoInterface::class)
        assertNotNull(concreteClass)
        assertTrue(concreteClass.isSubclassOf(TestDtoInterface::class))
        assertEquals(
            setOf(TestDtoInterface::class, Any::class),
            concreteClass.supertypes.map { it.classifier as KClass<*> }.toSet()
        )
        assertEquals(1, concreteClass.constructors.size)
        assertEquals(0, concreteClass.constructors.first().parameters.size)
        assertEquals(
            setOf("name", "age", "email"),
            concreteClass.memberProperties.map { it.name }.toSet()
        )
        assertEquals(
            setOf("toString", "hashCode", "equals"),
            concreteClass.functions.map { it.name }.toSet()
        )
    }
}