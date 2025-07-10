package com.runninglane.dto.buddy.test.cases.naming

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.naming.NamingStrategy
import kotlin.reflect.KClass
import kotlin.test.Test

class NamingStrategyTest {
    class TestNamingStrategy : NamingStrategy {
        override fun buildPackageName(baseClass: KClass<*>): String {
            return "com.example.jlp123456789.dto.customized"
        }

        override fun buildClassName(baseClass: KClass<*>): String {
            return "CustomName"
        }
    }

    interface AnyInterface

    @Test
    fun shouldCustomizeNameCorrectly() {
        val dtoBuddy = DtoBuddy(TestNamingStrategy())
        val concreteClass = dtoBuddy.implement(AnyInterface::class)
        assert(concreteClass.simpleName == "CustomName")
        assert(concreteClass.java.`package`.name == "com.example.jlp123456789.dto.customized")
    }
}