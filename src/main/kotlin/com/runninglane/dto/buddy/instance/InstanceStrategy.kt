package com.runninglane.dto.buddy.instance

interface InstanceStrategy {
    /**
     * Creates a new instance of a DTO class and populates it with the provided parameters
     *
     * @param concrete The class to instantiate
     * @return A new instance of the DTO class
     */
    @Suppress("UNCHECKED_CAST")
    fun create(concrete: Class<*>): Any

    /**
     * Populates an existing DTO instance with values from the provided parameter map
     *
     * @param dto The DTO instance to populate
     * @param params Map of property names to values
     */
    fun populate(dto: Any, params: Map<String, Any?>)
}