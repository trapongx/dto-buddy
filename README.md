# DtoBuddy

DtoBuddy is a utility for dynamically creating concrete implementations of Data Transfer Objects (DTOs) from interfaces or abstract classes.

## Contract

DtoBuddy follows a specific contract when generating DTO implementations:

### 1. Return Type
- DtoBuddy will always return a concrete class (not an interface or abstract class)

### 2. Base Class Requirements
- Base class must be public and open (not final)

### 3. Property Implementation Rules
- For each property in the base class, it must meet one of these conditions to be implemented:
  - Has only abstract getter and no setter
  - Has abstract getter and abstract setter
- Properties that don't meet these conditions will be ignored
  - If ignored properties are abstract, the generation will fail (abstract members can't exist in concrete classes)
  - If ignored properties are concrete and inherited from the base class, their behavior is not guaranteed
