package com.runninglane.dto.buddy.javainterop.bytecode.compile

import com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
import com.runninglane.dto.buddy.bytecode.compile.CompilationSession
import com.runninglane.dto.buddy.bytecode.validateContractCompliance
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import com.runninglane.dto.buddy.javainterop.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.javainterop.bytecode.ThreeStepsByteCodeStrategyCompliment
import com.runninglane.dto.buddy.javainterop.bytecode.kotlin
import com.squareup.javapoet.*
import java.lang.reflect.*
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

open class CompileJavaByteCodeStrategyCompliment() : ThreeStepsByteCodeStrategyCompliment<TypeSpec.Builder> {
    /**
     * Create a TypeSpec builder based on the source class
     * - For interfaces: creates a class implementing the interface
     * - For abstract classes: creates a subclass of the abstract class
     * - For concrete classes: creates a subclass with all properties made mutable
     */
    override fun defineClass(
        baseClass: Class<*>,
        typeParams: List<Class<*>>?,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): TypeSpec.Builder {
        // Validate that base class is public
        validateBaseClass(baseClass.kotlin)

        // Create a parameterized type if type parameters are provided
        val baseType: TypeName = if (typeParams?.isNotEmpty() == true) {
            // Map type parameters to their concrete types
            ParameterizedTypeName.get(
                ClassName.get(baseClass),
                *typeParams.map { TypeName.get(it) }.toTypedArray()
            )
        } else {
            TypeName.get(baseClass)
        }

        val classBuilder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .apply {
                if (baseClass.isInterface) {
                    addSuperinterface(baseType)
                } else {
                    superclass(baseType)
                }
            }

        val annotationSpec = AnnotationSpec.builder(DtoBuddyGenerated::class.java)
        return classBuilder.addAnnotation(annotationSpec.build())
    }

    /**
     * Adds properties and their getters/setters using KotlinPoet
     * @param typeParamsMapByName Optional map of type parameter names to their actual types for generic classes
     */
    override fun implementGetterSetterMethods(
        builder: TypeSpec.Builder,
        properties: List<PropertyDescriptor>,
        typeParamsMapByName: Map<String, Class<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        var classBuilder = builder

        // Validate all properties
        properties.forEach { it.kotlin().validateContractCompliance() }

        // Add all properties that need implementation
        for (property in properties.filter { it.kotlin().shouldImplement() }) {
            // Check if this property type references a generic type parameter that needs to be resolved
            val typeName = resolveTypeName(property.type, typeParamsMapByName)

            // Add a private field for the property
            val fieldName = "_${property.name}"
            val fieldSpec = FieldSpec.builder(
                typeName,
                fieldName,
                Modifier.PRIVATE
            )

            classBuilder = classBuilder.addField(fieldSpec.build())

            // Implement the getter method
            if (!property.hasConcreteGetter) {
                val getterMethod = MethodSpec.methodBuilder(property.getter!!.name)
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(typeName)
                    .let {
                        if (!property.isNullable) {
                            it.addAnnotation(ClassName.get("javax.validation.constraints", "NotNull"))
                        } else it
                    }
                    .addStatement("return $fieldName")
                    .build()
                classBuilder = classBuilder.addMethod(getterMethod)
            }

            // Implement the setter method if it exists
            val valueParamName = property.setter?.kotlinFunction?.valueParameters?.firstOrNull()?.name ?: "value"
            val escapedParamName = escapeReservedWord(valueParamName)
            val setterMethod = MethodSpec.methodBuilder("set${property.name.replaceFirstChar { it.uppercase() }}")
                .let {
                    it.takeIf { property.setter != null }?.addAnnotation(Override::class.java)
                        ?: it
                }
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    ParameterSpec.builder(typeName, escapedParamName)
                        .let {
                            if (!property.isNullable) {
                                it.addAnnotation(ClassName.get("javax.validation.constraints", "NotNull"))
                            } else it
                        }
                        .build()
                )
                .addStatement("this.$fieldName = $escapedParamName")
                .build()
            classBuilder = classBuilder.addMethod(setterMethod)
        }

        return classBuilder
    }

    /**
     * Generates Kotlin source code and compiles it using the CompilationSession
     */
    override fun loadJavaClass(
        builder: TypeSpec.Builder,
        packageName: String,
        className: String,
        dataCollector: Any?
    ): Class<*> {
        try {
            // Create a JavaFile (Java source file)
            val javaFile = JavaFile.builder(packageName, builder.build())
                .build()

            // Convert to source code string
            val sourceCode = javaFile.toString()

            // Compile and load the generated Java class with the known class name
            return CompilationSession.compileAndLoad(sourceCode, className, packageName, "java")
        } catch (e: Exception) {
            throw DtoBuddySystemException("Failed to compile and load generated Java class: ${e.message}", e)
        }
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
    override fun handleOtherAbstractMethods(
        builder: TypeSpec.Builder,
        methods: List<Method>,
        typeParamsMapByName: Map<String, Class<*>>?,
        dataCollector: Any?
    ): TypeSpec.Builder {
        if (methods.isNotEmpty()) {
            throw DtoBuddyBadInputException(
                "The following methods are not implemented: ${methods.map { it.name }}"
            )
        }

        return builder
    }

    /**
     * Escapes Java reserved words by adding an underscore suffix
     */
    private fun escapeReservedWord(name: String): String {
        val reservedWords = setOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while"
        )
        return if (name in reservedWords) "${name}_" else name
    }

    /**
     * Resolves a KType to a JavaPoet TypeName, handling generic type parameters
     */
    private fun resolveTypeName(type: Type, typeParamsMapByName: Map<String, Class<*>>?): TypeName {
        return when (type) {
            // Case 1: Direct type parameter (e.g., T)  
            is TypeVariable<*> -> {
                val paramName = type.name
                val concreteType = typeParamsMapByName?.get(paramName)
                    ?: throw DtoBuddyBadInputException(">$type< is not mapped to a concrete type. Please provide a mapping for it.")

                // Create a TypeName from the concrete type
                val typeName = TypeName.get(concreteType)
                if (typeName.isPrimitive) {
                    typeName.box()
                } else {
                    typeName
                }
            }

            // Case 2: Generic class with type arguments (e.g., List<T>)
            is ParameterizedType -> {
                // Get the raw type (e.g., List)
                val rawType = type.rawType as Class<*>

                // Process each type argument
                val typeArguments = type.actualTypeArguments.map { argType ->
                    when (argType) {
                        is WildcardType -> {
                            // Handle wildcard type (?)
                            WildcardTypeName.subtypeOf(Object::class.java)
                        }

                        else -> {
                            // Recursively resolve the type argument
                            resolveTypeName(argType, typeParamsMapByName)
                        }
                    }
                }.toTypedArray()

                // Create a parameterized type name
                ParameterizedTypeName.get(ClassName.get(rawType), *typeArguments)
            }

            // For regular classes
            is Class<*> -> TypeName.get(type)

            // Fallback for other cases
            else -> TypeName.get(Any::class.java)
        }
    }
}
