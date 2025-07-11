package com.runninglane.dto.buddy.instance

import kotlin.reflect.KClass

interface InstanceStrategy {
    /**
     * Creates a new instance of a DTO class and populates it with the provided parameters
     *
     * @param concrete The class to instantiate
     * @return A new instance of the DTO class
     */
    @JvmSynthetic
    fun create(concrete: KClass<*>): Any

    fun create(concrete: Class<*>): Any = create(concrete.kotlin)

    /**
     * Populates an existing DTO instance with values from the provided parameter map
     *
     * @param dto The DTO instance to populate
     * @param params Map of property names to values
     */
    fun populate(dto: Any, params: Map<String, Any?>)
}