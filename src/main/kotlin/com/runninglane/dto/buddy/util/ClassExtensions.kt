package com.runninglane.dto.buddy.util

/**
 * Capitalizes the first character of a string
 */
fun String.capitalize(): String = replaceFirstChar { it.uppercase() }
