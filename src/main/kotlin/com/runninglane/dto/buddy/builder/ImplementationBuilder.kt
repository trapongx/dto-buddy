package com.runninglane.dto.buddy.builder

import com.runninglane.dto.buddy.DtoBuddy

class ImplementationBuilder() {
    private var baseClass: Class<*>? = null
    private var typeParams: List<Class<*>>? = null
    private var packageName: String? = null
    private var name: String? = null
    private var nameSuffix: String? = null

    constructor(baseClass: Class<*>) : this() {
        this.baseClass = baseClass
    }

    fun withBaseClass(baseClass: Class<*>): ImplementationBuilder = this.also {
        it.baseClass = baseClass
    }

    fun withTypeParams(typeParams: List<Class<*>>): ImplementationBuilder = this.also {
        it.typeParams = typeParams
    }

    fun withPackageName(packageName: String): ImplementationBuilder = this.also {
        it.packageName = packageName
    }

    fun withName(name: String): ImplementationBuilder = this.also {
        it.name = name
    }

    fun withNameSuffix(nameSuffix: String): ImplementationBuilder = this.also {
        it.nameSuffix = nameSuffix
    }

    fun implement(): Class<*> {
        requireNotNull(baseClass) { "Interface must be specified" }

        return DtoBuddy.implement(baseClass!!, typeParams, packageName, name, nameSuffix)
    }
}
