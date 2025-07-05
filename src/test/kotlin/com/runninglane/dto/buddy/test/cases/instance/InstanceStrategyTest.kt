package com.runninglane.dto.buddy.test.cases.instance

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceStrategyTest {
    abstract class TestDto {
        var flag: Boolean = false
        abstract var name: String
    }

    class TestInstanceStrategy : DefaultInstanceStrategy() {
        override fun create(concrete: Class<*>): Any {
            return super.create(concrete).also {
                (it as TestDto).flag = true
            }
        }

        override fun populate(dto: Any, params: Map<String, Any?>) {
            super.populate(dto, params.mapValues { it.value?.toString()?.uppercase() })
        }
    }

    @Test
    fun shouldCustomizeInstanceStrategyCorrectly() {
        val dtoBuddy = DtoBuddy(TestInstanceStrategy())
        val concreteClass = dtoBuddy.implement(TestDto::class.java)
        val dto = dtoBuddy.create<TestDto>(concreteClass, mapOf("name" to "hello"))
        assertEquals(true, dto.flag)
        assertEquals("HELLO", dto.name)
        dtoBuddy.populate(dto, mapOf("name" to "world"))
        assertEquals("WORLD", dto.name)
    }
}