package com.runninglane.dto.buddy

object DtoBuddy {
    /**
     * Return a new generated class that implement `interface` but have all fields provided with getter and setter.
     * For each abstract property, it aims to provide 3 class members including field, getter, and setter.
     * For concrete properties, it will just inherit them.
     * The `interface` class must not have partially implemented property that is mutable but only have one of
     * its getter or setter abstract.
     * Generated class will be annotated with #com.runninglane.dto.buddy.annotation.DtoBuddyGenerated
     * These are examples of valid `interface` parameter and their expected generated classes.
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
     * These are example `interface` classes that will cause an exception to type DtoBuddyBadInputException.
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
     * @param `interface`
     * @param packageName, default value is the same package name as of the `interface` param
     * @param name, default value is the name of the `interface` param with "$Dto" added at the end
     * @param nameSuffix, there might be some scenario to generate classes for the same interface multiple time,
     * nameSuffix will be added at the very end of their name to avoid name collision. For this project, the test cases
     * make use of this parameter a lot.
     * @return generated class of type Class<*>
     */
    fun implement(
        `interface`: Class<*>,
        packageName: String = `interface`.packageName,
        name: String = `interface`.simpleName + "\$Dto",
        nameSuffix: String? = null
    ): Class<*> = TODO("Not yet implemented")

    fun <DTO> create(concrete: Class<*>, params: Map<String, Any?>): DTO = TODO("Not yet implemented")

    fun <DTO> populate(dto: DTO, params: Map<String, Any?>) { TODO("Not yet implemented") }
}