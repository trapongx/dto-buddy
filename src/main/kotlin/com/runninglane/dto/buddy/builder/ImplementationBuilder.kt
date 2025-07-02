package com.runninglane.dto.buddy.builder

import com.runninglane.dto.buddy.DtoBuddy

class ImplementationBuilder() {
    private var `interface`: Class<*>? = null
    private var packageName: String? = null
    private var name: String? = null
    private var nameSuffix: String? = null

    constructor(`interface`: Class<*>) : this() {
        this.`interface` = `interface`
    }

    fun withInterface(`interface`: Class<*>): ImplementationBuilder {
        this.`interface` = `interface`
        return this
    }

    fun withPackageName(packageName: String): ImplementationBuilder {
        this.packageName = packageName
        return this
    }

    fun withName(name: String): ImplementationBuilder {
        this.name = name
        return this
    }

    fun withNameSuffix(nameSuffix: String): ImplementationBuilder {
        this.nameSuffix = nameSuffix
        return this
    }

    fun implement(): Class<*> {
        requireNotNull(`interface`) { "Interface must be specified" }

        return DtoBuddy.implement(`interface`!!, packageName, name, nameSuffix)
    }
}
