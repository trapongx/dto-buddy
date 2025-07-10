package com.runninglane.dto.buddy.naming

import kotlin.reflect.KClass

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
    fun buildPackageName(baseClass: KClass<*>): String

    /**
     * Determines the name of the generated class
     *
     * @param baseClass The base class being implemented
     * @return The name for the generated class
     */
    fun buildClassName(baseClass: KClass<*>): String
}
