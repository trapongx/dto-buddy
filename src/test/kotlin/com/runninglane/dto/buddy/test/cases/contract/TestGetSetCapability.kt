package com.runninglane.dto.buddy.test.cases.contract

import com.runninglane.dto.buddy.DtoBuddy
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

interface TestGetSetCapability {
    fun testGetSet(dtoBuddy: DtoBuddy, concreteClass: KClass<*>, testGetSetInJavaStyle: Boolean) {
        val dto = dtoBuddy.create<Any>(concreteClass, mapOf("name" to "Test"))
        if (testGetSetInJavaStyle) {
            val getter = concreteClass.functions.find { it.name == "getName" }
            assertNotNull(getter)
            assertEquals(0, getter.valueParameters.size)
            assertEquals(KVisibility.PUBLIC, getter.visibility)
            assertEquals("Test", getter.call(dto))

            val setter = concreteClass.functions.find {
                it.name == "setName" && it.valueParameters.size == 1 && it.visibility == KVisibility.PUBLIC
            }
            assertNotNull(setter)
            assertEquals(1, setter.valueParameters.size)
            setter.call(dto, "Test2")
            assertEquals("Test2", getter.call(dto))
        } else {
            val property = concreteClass.members.find { it.name == "name" }
            assertIs<KMutableProperty1<Any, String>>(property)
            assertEquals(KVisibility.PUBLIC, property.visibility)
            assertEquals("Test", property.get(dto))
            property.set(dto, "Test2")
            assertEquals("Test2", property.get(dto))
        }
    }
}