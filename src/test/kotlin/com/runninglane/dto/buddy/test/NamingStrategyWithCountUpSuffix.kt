package com.runninglane.dto.buddy.test

import com.runninglane.dto.buddy.naming.DefaultNamingStrategy
import kotlin.reflect.KClass

class NamingStrategyWithCountUpSuffix : DefaultNamingStrategy() {
    private var nameSuffix: Int = 0

    override fun buildClassName(baseClass: KClass<*>): String {
        return "${super.buildClassName(baseClass)}${nameSuffix++}"
    }
}