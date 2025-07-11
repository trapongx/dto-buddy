package com.runninglane.dto.buddy.test

import com.runninglane.dto.buddy.naming.DefaultNamingStrategy

class NamingStrategyWithCountUpSuffix : DefaultNamingStrategy() {
    private var nameSuffix: Int = 0

    override fun buildClassName(baseClass: Class<*>): String {
        return "${super.buildClassName(baseClass)}${nameSuffix++}"
    }
}