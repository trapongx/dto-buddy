package com.runninglane.dto.buddy.test.cases.validate

import com.runninglane.dto.buddy.DtoBuddy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class PartiallyImplementedPropertyValidationTest {

    abstract class AbstractClassWithConcreteGetterAndAbstractSetter {
        fun getName(): String = "John"
        abstract fun setName(name: String)
    }

    abstract class AbstractClassWithAbstractGetterAndConcreteSetter {
        abstract fun getName(): String
        fun setName(name: String) { TODO("Not yet implemented") }
    }

    @Test
    fun shouldFailWhenGetterIsConcreteAndSetterIsAbstract() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(AbstractClassWithConcreteGetterAndAbstractSetter::class.java)
        }
    }

    @Test
    fun shouldFailWhenGetterIsAbstractAndSetterIsConcrete() {
        assertThrows<DtoBuddyBadInputException> {
            DtoBuddy.implement(AbstractClassWithAbstractGetterAndConcreteSetter::class.java)
        }
    }
}