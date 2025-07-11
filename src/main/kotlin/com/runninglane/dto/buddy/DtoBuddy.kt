package com.runninglane.dto.buddy

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.DefaultByteCodeStrategy
import com.runninglane.dto.buddy.exception.DtoBuddyBadInputException
import com.runninglane.dto.buddy.exception.DtoBuddySystemException
import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy
import com.runninglane.dto.buddy.instance.InstanceStrategy
import com.runninglane.dto.buddy.naming.DefaultNamingStrategy
import com.runninglane.dto.buddy.naming.NamingStrategy
import kotlin.reflect.KClass

/**
 * DtoBuddy provides utilities for working with Data Transfer Objects (DTOs).
 * It can dynamically create concrete implementations of interfaces or abstract classes,
 * instantiate those implementations, and populate their properties.
 */
class DtoBuddy(
    var namingStrategy: NamingStrategy = DefaultNamingStrategy(),
    var byteCodeStrategy: ByteCodeStrategy = DefaultByteCodeStrategy(),
    var instanceStrategy: InstanceStrategy = DefaultInstanceStrategy()
) {
    constructor(namingStrategy: NamingStrategy) : this(
        namingStrategy,
        DefaultByteCodeStrategy(),
        DefaultInstanceStrategy()
    )

    constructor(byteCodeStrategy: ByteCodeStrategy) : this(
        DefaultNamingStrategy(),
        byteCodeStrategy,
        DefaultInstanceStrategy()
    )

    constructor(instanceStrategy: InstanceStrategy) : this(
        DefaultNamingStrategy(),
        DefaultByteCodeStrategy(),
        instanceStrategy
    )

    // Cache for generated classes to avoid regenerating the same class
    private val classCache = mutableMapOf<String, Pair<KClass<*>, KClass<*>>>()

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
     * @param typeParams when base class is generic type
     * @return generated class
     */
    @JvmSynthetic
    fun implement(
        baseClass: KClass<*>,
        typeParams: List<KClass<*>>? = null
    ): KClass<*> {
        val packageName = namingStrategy.buildPackageName(baseClass.java)
        val className = namingStrategy.buildClassName(baseClass.java)

        // Generate cache key
        val cacheKey = "$packageName.$className"

        // Check cache first
        classCache[cacheKey]?.also { (prevBaseClass, prevGenClass) ->
            if (prevBaseClass != baseClass) {
                throw DtoBuddySystemException("Class $cacheKey is already implemented for ${prevGenClass.qualifiedName}")
            }
            return prevGenClass
        }

        if((typeParams?.size ?: 0) != baseClass.typeParameters.size) {
            throw DtoBuddyBadInputException("Type parameter count mismatch: ${typeParams?.size} != ${baseClass.typeParameters.size}")
        }

        return try {
            byteCodeStrategy.implement(baseClass, typeParams, packageName, className)
                .also { generatedClass -> classCache[cacheKey] = baseClass to generatedClass }
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to implement DTO: ${e.message}")
            }
        }
    }

    /**
     * Creates a new instance of a DTO class and populates it with the provided parameters
     *
     * @param concrete The class to instantiate
     * @param params Map of property names to values
     * @return A new instance of the DTO class with populated properties
     */
    @JvmSynthetic
    @Suppress("UNCHECKED_CAST")
    fun <DTO> create(concrete: KClass<*>, params: Map<String, Any?>? = null): DTO {
        try {
            // Create a new instance
            val instance = instanceStrategy.create(concrete)

            // Populate the properties
            params?.also { instanceStrategy.populate(instance, it) }

            return instance as DTO
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to create DTO instance: ${e.message}", e)
            }
        }
    }

    /**
     * Populates an existing DTO instance with values from the provided parameter map
     *
     * @param dto The DTO instance to populate
     * @param params Map of property names to values
     */
    fun populate(dto: Any, params: Map<String, Any?>) {
        try {
            // Populate the properties
            instanceStrategy.populate(dto, params)
        } catch (e: Exception) {
            when (e) {
                is DtoBuddyBadInputException, is DtoBuddySystemException -> throw e
                else -> throw DtoBuddySystemException("Failed to populate DTO: ${e.message}")
            }
        }
    }
}