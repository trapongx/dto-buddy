package com.runninglane.dto.buddy.bytecode.codegen

import com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.validateContractCompliance
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.*
import kotlin.reflect.full.valueParameters

/**
 * Implementation of ByteCodeStrategy that uses KotlinPoet to generate source code
 * and then compiles it using the embedded Kotlin compiler.
 */
open class KotlinCodeGenerator : CodeGenerator<TypeSpec.Builder> {
    /**
     * Create a TypeSpec builder based on the source class
     * - For interfaces: creates a class implementing the interface
     * - For abstract classes: creates a subclass of the abstract class
     * - For concrete classes: creates a subclass with all properties made mutable
     */
    override fun defineClass(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): TypeSpec.Builder {
        // Validate that base class is public
        validateBaseClass(baseClass)

        // Create a parameterized type if type parameters are provided
        val baseTypeName = if (typeParams?.isNotEmpty() == true) {
            // Map type parameters to their concrete types
            baseClass.asClassName().parameterizedBy(
                typeParams.map { it.asTypeName() }
            )
        } else {
            baseClass.asTypeName()
        }

        val classBuilder = when {
            baseClass.java.isInterface -> {
                TypeSpec.classBuilder(className)
                    .addSuperinterface(baseTypeName)
            }

            else -> {
                TypeSpec.classBuilder(className)
                    .superclass(baseTypeName)
            }
        }

        val annotationSpec = AnnotationSpec.builder(DtoBuddyGenerated::class).build()
        return classBuilder.addAnnotation(annotationSpec)
    }

    /**
     * Adds properties and their getters/setters using KotlinPoet
     * @param typeParamsMapByName Optional map of type parameter names to their actual types for generic classes
     */
    override fun implementProperties(
        builder: TypeSpec.Builder,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        var classBuilder = builder

        // Validate all properties
        properties.forEach { it.validateContractCompliance() }

        // Add all properties that need implementation
        for (property in properties.filter { it.shouldImplement() }) {
            // Check if this property type references a generic type parameter that needs to be resolved
            val typeName = resolveTypeName(property.type, typeParamsMapByName)

            // Initialize the property with default value to satisfy Kotlin compiler
            val defaultValue = getDefaultValueForType(typeName)
            
            if (property.kProperty != null) {
                // For Kotlin properties, use the standard property implementation
                val propertySpec = PropertySpec.builder(property.name, typeName)
                    .mutable(true)
                    .addModifiers(KModifier.OVERRIDE)
                    .let {
                        if (defaultValue == null && !property.isNullable)
                            it.addModifiers(KModifier.LATEINIT)
                        else
                            it.initializer(defaultValue ?: "null")
                    }
                    .let {
                        // Add @JvmName for boolean properties starting with 'is'
                        if (property.name.startsWith("is") && typeName.toString() == "kotlin.Boolean") {
                            it.getter(
                                FunSpec.getterBuilder()
                                    .addAnnotation(
                                        AnnotationSpec.builder(Suppress::class)
                                            .addMember("\"INAPPLICABLE_JVM_NAME\"")
                                            .build()
                                    )
                                    .addAnnotation(
                                        AnnotationSpec.builder(JvmName::class)
                                            .addMember(
                                                "name = %S",
                                                "get${property.name.replaceFirstChar { it.uppercase() }}"
                                            )
                                            .build()
                                    )
                                    .addStatement("return field")
                                    .build()
                            )
                        } else it
                    }

                // Add the property to the class
                classBuilder = classBuilder.addProperty(propertySpec.build())
            } else {
                // For Java-style getter/setter, we need to implement explicit methods
                // Add a private field for the property
                val fieldName = "_${property.name}"
                val fieldSpec = PropertySpec.builder(fieldName, typeName)
                    .mutable(true)
                    .addModifiers(KModifier.PRIVATE)
                    .let {
                        if (defaultValue == null && !property.isNullable)
                            it.addModifiers(KModifier.LATEINIT)
                        else
                            it.initializer(defaultValue ?: "null")
                    }
                    .build()
                classBuilder = classBuilder.addProperty(fieldSpec)

                // Implement the getter method
                if (!property.hasConcreteGetter) {
                    val getterMethod = FunSpec.builder(property.getter!!.name)
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(typeName)
                        .addStatement("return $fieldName")
                        .build()
                    classBuilder = classBuilder.addFunction(getterMethod)
                }

                // Implement the setter method if it exists
                val valueParamName = property.setter?.valueParameters?.firstOrNull()?.name ?: "value"
                val escapedParamName = escapeReservedWord(valueParamName)
                val setterMethod = FunSpec.builder("set${property.name.replaceFirstChar { it.uppercase() }}")
                    .let {
                        it.takeIf { property.setter != null }?.addModifiers(KModifier.OVERRIDE)
                            ?: it
                    }
                    .addParameter(escapedParamName, typeName)
                    .addStatement("$fieldName = $escapedParamName")
                    .build()
                classBuilder = classBuilder.addFunction(setterMethod)
            }
        }

        return classBuilder
    }

    /**
     * Validates that a base class meets the requirements:
     * - Must be public
     * - If class (not interface), must be open/abstract
     */
    private fun validateBaseClass(baseClass: KClass<*>) {
        if (baseClass.visibility != KVisibility.PUBLIC) {
            throw DtoBuddyBadInputException(
                "Base class ${baseClass.qualifiedName} must be public."
            )
        }

        // For classes (not interfaces), check if they are open/abstract and not final
        if (baseClass.isFinal) {
            throw DtoBuddyBadInputException(
                "Base class ${baseClass.qualifiedName} must not be final class."
            )
        }
    }

    /**
     * Handles non-property abstract functions by generating default implementations
     * For functions returning non-void types, returns a default value based on return type
     */
    override fun handleOtherAbstractMembers(
        builder: TypeSpec.Builder,
        properties: List<KProperty<*>>,
        functions: List<KFunction<*>>,
        typeParamsMapByName: Map<String, KClass<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        if (properties.size + functions.size > 0) {
            throw DtoBuddyBadInputException(
                "The following properties and functions are not implemented: ${
                    functions.map { it.name } + properties.map { it.name }
                }"
            )
        }

        return builder
    }

    /**
     * Escapes Kotlin reserved words with back ticks
     */
    protected fun escapeReservedWord(name: String): String {
        val reservedWords = setOf(
            "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if",
            "in", "interface", "is", "null", "object", "package", "return", "super", "this",
            "throw", "true", "try", "typealias", "typeof", "val", "var", "when", "while"
        )
        return if (name in reservedWords) "`$name`" else name
    }

    /**
    * Provides default values for common return types
     */
    protected fun getDefaultValueForType(typeName: TypeName): String? {
        return when {
            // For non-nullable primitive types, use appropriate default values
            typeName.toString() == "kotlin.String" -> "\"\""
            typeName.toString() == "kotlin.Int" -> "0"
            typeName.toString() == "kotlin.Long" -> "0L"
            typeName.toString() == "kotlin.Double" -> "0.0"
            typeName.toString() == "kotlin.Float" -> "0.0f"
            typeName.toString() == "kotlin.Boolean" -> "false"
            typeName.toString() == "kotlin.Char" -> "' '"
            typeName.toString() == "kotlin.Byte" -> "0"
            typeName.toString() == "kotlin.Short" -> "0"

            // For collection types, return empty collections
            typeName.toString().startsWith("kotlin.collections.List") -> "emptyList()"
            typeName.toString().startsWith("kotlin.collections.MutableList") -> "mutableListOf()"
            typeName.toString().startsWith("kotlin.collections.Set") -> "emptySet()"
            typeName.toString().startsWith("kotlin.collections.MutableSet") -> "mutableSetOf()"
            typeName.toString().startsWith("kotlin.collections.Map") -> "emptyMap()"
            typeName.toString().startsWith("kotlin.collections.MutableMap") -> "mutableMapOf()"

            // For other non-nullable reference types, use null with casting
            else -> null
        }
    }

    /**
     * Resolves a KType to a TypeName, handling generic type parameters
     * If the type is a type parameter, it will be resolved using the typeParamsMapByName
     * Also handles complex generic types like List<T> by recursively resolving type arguments
     */
    protected fun resolveTypeName(type: KType, typeParamsMapByName: Map<String, KClass<*>>?): TypeName {
        // Debug information to help track type resolution
        // println("Resolving type: $type, classifier: ${type.classifier}, jvmErasure: ${type.jvmErasure}")
        val classifier = type.classifier
        // Get the full original type string to preserve exact type information
        val fullTypeStr = type.toString()
        // Extract just the class name part (before any generic parameters)
        val exactTypeStr = fullTypeStr.substringBefore('<')

        return when {
            // Case 1: Direct type parameter (e.g., T)
            classifier is KTypeParameter -> {
                val paramName = classifier.name
                val concreteType = typeParamsMapByName?.get(paramName)
                    ?: throw DtoBuddyBadInputException(">$classifier< is not mapped to a concrete type. Please provide a mapping for it.")

                // Create a TypeName from the concrete type, preserving nullability
                concreteType.asTypeName().considerJavaNullability(type)
            }

            // Case 2: Generic class with type arguments (e.g., List<T>)
            classifier is KClass<*> && type.arguments.isNotEmpty()-> {
                // Use a more precise approach to get the exact type class name
                // This preserves properties like mutability that might be lost in classifier
                val rawTypeName = if (exactTypeStr != classifier.qualifiedName) {
                    // The type string doesn't match the classifier's qualified name
                    // This indicates we need to use the exact type from the string
                    val packageName = exactTypeStr.substringBeforeLast('.', "").takeIf { it.isNotEmpty() } ?: ""  
                    val simpleClassName = exactTypeStr.substringAfterLast('.')

                    // Only create a custom ClassName if we have valid package and class names
                    if (packageName.isNotEmpty() && simpleClassName.isNotEmpty()) {
                        ClassName(packageName, simpleClassName)
                    } else {
                        classifier.asClassName()
                    }
                } else {
                    // Type string matches classifier's qualified name, so just use the classifier directly
                    classifier.asClassName()
                }

                // Process each type argument
                val typeArguments = type.arguments.map { projection ->
                    val argType = projection.type
                    if (argType == null) {
                        // Handle star projection (*)
                        STAR
                    } else {
                        // Recursively resolve the type argument
                        resolveTypeName(argType, typeParamsMapByName)
                    }
                }

                // Create a parameterized type name
                rawTypeName.parameterizedBy(typeArguments).considerJavaNullability(type)
            }

            // For regular types without type arguments, just use the standard asTypeName
            else -> type.asTypeName().considerJavaNullability(type)
        }
    }

    protected fun TypeName.considerJavaNullability(type: KType): TypeName = when {
        type.isMarkedNullable || type.toString().endsWith("!") -> copy(nullable = true)
        else -> this
    }

    override fun writeToString(
        builder: TypeSpec.Builder,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): String {
        // Create a FileSpec (Kotlin source file)
        val fileSpec = FileSpec.builder(packageName, "$className.kt")
            .addType(builder.build())
            .build()

        // Convert to source code string
        return fileSpec.toString()
    }
}
