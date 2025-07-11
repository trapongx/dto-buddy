package com.runninglane.dto.buddy.naming

/**
 * Interface for customizing how DtoBuddy generates DTOs
 */
interface NamingStrategy {
    /**
     * Determines the package name of the generated class
     *
     * @param baseClass The base class being implemented
     * @return The name for the generated class
     */
    fun buildPackageName(baseClass: Class<*>): String

    /**
     * Determines the name of the generated class
     *
     * @param baseClass The base class being implemented
     * @return The name for the generated class
     */
    fun buildClassName(baseClass: Class<*>): String
}
