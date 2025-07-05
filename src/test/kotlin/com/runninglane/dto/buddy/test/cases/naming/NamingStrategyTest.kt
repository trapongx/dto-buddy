package com.runninglane.dto.buddy.test.cases.naming

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.naming.NamingStrategy
import kotlin.test.Test

class NamingStrategyTest {
    class TestNamingStrategy : NamingStrategy {
        override fun buildPackageName(baseClass: Class<*>): String {
            return "com.example.jlp123456789.dto.customized"
        }

        override fun buildClassName(baseClass: Class<*>): String {
            return "CustomName"
        }
    }

    interface AnyInterface

    @Test
    fun shouldCustomizeNameCorrectly() {
        val dtoBuddy = DtoBuddy(TestNamingStrategy())
        val concreteClass = dtoBuddy.implement(AnyInterface::class.java)
        assert(concreteClass.simpleName == "CustomName")
        assert(concreteClass.`package`.name == "com.example.jlp123456789.dto.customized")
    }
}