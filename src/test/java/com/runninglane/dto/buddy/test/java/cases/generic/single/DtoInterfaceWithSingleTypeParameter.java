package com.runninglane.dto.buddy.test.java.cases.generic.single;

public interface DtoInterfaceWithSingleTypeParameter<T> {
    String getName();
    Integer getAge();
    T getEmail();
}