package com.runninglane.dto.buddy.builder

import com.runninglane.dto.buddy.DtoBuddy

class ImplementationBuilder() {
    private var baseClass: Class<*>? = null
    private var packageName: String? = null
    private var name: String? = null
    private var nameSuffix: String? = null

    constructor(baseClass: Class<*>) : this() {
        this.baseClass = baseClass
    }

    fun withBaseClass(baseClass: Class<*>): ImplementationBuilder {
        this.baseClass = baseClass
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
        requireNotNull(baseClass) { "Interface must be specified" }

        return DtoBuddy.implement(baseClass!!, packageName, name, nameSuffix)
    }
}
