package com.runninglane.dto.buddy.builder

import com.runninglane.dto.buddy.DtoBuddy

class ImplementationBuilder {
    private val dtoBuddy: DtoBuddy
    private var baseClass: Class<*>? = null
    private var typeParams: List<Class<*>>? = null

    constructor(dtoBuddy: DtoBuddy) {
        this.dtoBuddy = dtoBuddy
    }

    constructor(dtoBuddy: DtoBuddy, baseClass: Class<*>) : this(dtoBuddy) {
        this.baseClass = baseClass
    }

    fun withBaseClass(baseClass: Class<*>): ImplementationBuilder = this.also {
        it.baseClass = baseClass
    }

    fun withTypeParams(typeParams: List<Class<*>>): ImplementationBuilder = this.also {
        it.typeParams = typeParams
    }

    fun implement(): Class<*> {
        requireNotNull(baseClass) { "Base class must be specified" }

        return dtoBuddy.implement(baseClass!!, typeParams)
    }
}
