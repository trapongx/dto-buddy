package com.runninglane.dto.buddy

import com.runninglane.dto.buddy.builder.ImplementationBuilder
import com.runninglane.dto.buddy.bytecode.ByteBuddyWrapper
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import java.lang.reflect.Modifier

/**
 * DtoBuddy provides utilities for working with Data Transfer Objects (DTOs).
 * It can dynamically create concrete implementations of interfaces or abstract classes,
 * instantiate those implementations, and populate their properties.
 */
object DtoBuddy {
    private val byteBuddyWrapper = ByteBuddyWrapper()

    // Cache for generated classes to avoid regenerating the same class
    private val classCache = mutableMapOf<String, Class<*>>()

    // Cache for property info to avoid reanalyzing classes (for implementation)
    private val propertiesCache = mutableMapOf<Class<*>, List<PropertyDescriptor>>()


    /**
     * Return a new generated class that implements base class but have all fields provided with getter and setter.
     * Or say, a class to instantiate mutable objects that are subclass of the base class.
     * For each abstract property, it aims to provide 3 class members including field, getter, and setter.
     * For concrete properties, it will just inherit them.
     * The base class must not have a partially implemented property that is mutable but only have one of
     * its getter or setter abstract.
     * Generated classes will be annotated with #com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
     * These are examples of valid base classes and their expected generated classes.
     * 
     * For this input
     * ```kotlin
     * interface Child {
     *    val id: Long
     *    var name: String?
     *    var parent: Parent
     * }
     * ```
     * or
     * ```java
     * interface Child {
     *    public Long getId();
     *
     *    public String getName();
     *    public void setName(String value);
     *
     *    public Parent getParent();
     *    public void setParent(Parent value);
     * }
     * ```
     * the output will be
     * ```java
     * @DtoBuddyGenerated
     * public class `Child$Dto` implements Child {
     *    private Long id;
     *    public Long getId() { return id; }
     *    public void setId(Long value) { this.id = value }
     *    
     *    private String name;
     *    public String getName() { return name; }
     *    public void setName(String value) { this.name = value }
     *    
     *    private String parent;
     *    public String getParent() { return parent; }
     *    public void setParent(String value) { this.parent = value }
     *    
     * }
     * ```
     * 
     * For this input
     * ```kotlin
     * abstract class Child {
     *    val id: Long = generateId()
     *    var name: String?
     *    var parent: Parent
     * }
     * ```
     * or
     * ```java
     * abstract class Child {
     *    private Long id = generateId();
     *    public Long getId() { return id; }
     *
     *    public abstract String getName();
     *    public abstract void setName(String value);
     *
     *    public abstract Parent getParent();
     *    public abstract void setParent(Parent value);
     * }
     * ```
     * the output will be
     * ```java
     * @DtoBuddyGenerated
     * public class `Child$Dto` extends Child() {
     *    private String name;
     *    public String getName() { return name; }
     *    public void setName(String value) { this.name = value }
     *
     *    private String parent;
     *    public String getParent() { return parent; }
     *    public void setParent(String value) { this.parent = value }
     * }
     * ```
     *
     * These are example base classes that will cause an exception to type DtoBuddyBadInputException.
     *
     * ```kotin
     * abstract class Child {
     *    private var _id: Long = generateId()
     *    fun getId() = _id
     *    abstract fun setId(value: Long)
     * }
     * ```
     *
     * ```java
     * abstract class Child {
     *    private Long id;
     *    public Long getId() { return id; }
     *    public abstract void setId(Long value);
     * }
     * ```
     *
     * @param baseClass
     * @param packageName, default value is the same package name as of the base class
     * @param name, default value is the name of the base class with "$Dto" added at the end
     * @param nameSuffix, there might be some scenario to generate classes for the same interface single time,
     * nameSuffix will be added at the very end of their name to avoid name collision. For this project, the test cases
     * make use of this parameter a lot.
     * @return generated class of type Class<*>
     */
    @JvmStatic
    fun implement(
        baseClass: Class<*>,
        typeParams: List<Class<*>>? = null,
        packageName: String? = null,
        name: String? = null,
        nameSuffix: String? = null
    ): Class<*> {
        val packageName = packageName ?: baseClass.packageName
        val name = name ?: "${baseClass.simpleName}\$Dto"
        val nameSuffix = nameSuffix ?: ""

        // Generate full class name
        val fullClassName = "$name$nameSuffix"
        val cacheKey = "$packageName.$fullClassName"

        // Check cache first
        classCache[cacheKey]?.let { return it }

        require((typeParams?.size ?: 0) == baseClass.typeParameters.size) {
            "Type parameter count mismatch: ${typeParams?.size} != ${baseClass.typeParameters.size}"
        }
        // Though cacheKey is not in classCache, it does not mean that the base class has never been analyzed before.
        // It's possible that the same baseClass passed in with different other parameters.
        val properties = propertiesCache.getOrPut(baseClass) {
            PropertyDescriptor.from(baseClass)
        }

        if (properties.isEmpty() && !baseClass.isInterface && Modifier.isAbstract(baseClass.modifiers)) {
            // All properties are already mutable and it's not an interface, return the original class
            classCache[cacheKey] = baseClass
            return baseClass
        }

        try {
            // Create dynamic type builder
            val builder = byteBuddyWrapper.createDynamicType(baseClass, typeParams, packageName, fullClassName)

            // Create a map of type parameter names to actual types
            val typeParamsMapByName = typeParams?.takeIf { it.isNotEmpty() }
                ?.let { typeParams ->
                    val typeParameterNames = baseClass.typeParameters.map { it.name }
                        typeParameterNames.zip(typeParams).toMap()
                } ?: emptyMap()

            // Implement properties
            val implementedBuilder = byteBuddyWrapper.implementProperties(builder, properties, typeParamsMapByName)

            // Load the generated class
            val generatedClass = byteBuddyWrapper.loadClass(implementedBuilder)

            // Cache the result
            classCache[cacheKey] = generatedClass

            return generatedClass
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to implement DTO: ${e.message}")
            }
        }
    }

    @JvmStatic
    fun implementor() = ImplementationBuilder()

    @JvmStatic
    fun implementor(baseClass: Class<*>) = ImplementationBuilder(baseClass)

    /**
     * Creates a new instance of a DTO class and populates it with the provided parameters
     *
     * @param concrete The class to instantiate
     * @param params Map of property names to values
     * @return A new instance of the DTO class with populated properties
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <DTO> create(concrete: Class<*>, params: Map<String, Any?>): DTO {
        try {
            // Create a new instance
            val instance = byteBuddyWrapper.createInstance<Any>(concrete)

            // Use the cached property info or analyze if not cached
            val properties = propertiesCache.getOrPut(concrete) {
                PropertyDescriptor.from(concrete)
            }

            // Populate the properties
            byteBuddyWrapper.populate(instance, properties, params)

            return instance as DTO
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to create DTO instance: ${e.message}")
            }
        }
    }

    /**
     * Populates an existing DTO instance with values from the provided parameter map
     *
     * @param dto The DTO instance to populate
     * @param params Map of property names to values
     */
    @JvmStatic
    fun <DTO> populate(dto: DTO, params: Map<String, Any?>) {
        try {
            val dtoClass = dto!!::class.java

            // For population, we need ALL properties, not just those to implement
            // Use the all-properties cache for population
            val properties = propertiesCache.getOrPut(dtoClass) {
                PropertyDescriptor.from(dtoClass)
            }

            // Populate the properties
            byteBuddyWrapper.populate(dto, properties, params)
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to populate DTO: ${e.message}")
            }
        }
    }
}