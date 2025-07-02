package com.runninglane.dto.buddy.test.cases.generic.single

interface DtoInterfaceWithSingleTypeParameter<T> {
    val name: String
    val age: Int
    val email: T?
}