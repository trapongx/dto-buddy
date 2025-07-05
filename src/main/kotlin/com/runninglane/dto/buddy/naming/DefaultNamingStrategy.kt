package com.runninglane.dto.buddy.naming

/**
 * Default implementation of DtoBuddyCustomizer
 * Uses the standard DtoBuddy contract rules
 */
open class DefaultNamingStrategy : NamingStrategy {
    /**
     * The default implementation uses the same package name as of the base class
     *
     * @param baseClass The base class being implemented
     * @return The name for the generated class
     */
    override fun buildPackageName(baseClass: Class<*>): String {
        return baseClass.`package`?.name ?: ""
    }

    /**
     * The default implementation adds "$Dto" suffix to the base class name
     *
     * @param baseClass The base class being implemented
     * @return The name for the generated class
     */
    override fun buildClassName(baseClass: Class<*>): String {
        return "${baseClass.simpleName}\$Dto"
    }
}