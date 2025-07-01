package com.runninglane.dto.buddy.test.cases.inner

interface InnerDtoInterfaceEncloser {
    interface InnerDtoInterface {
        val name: String
        val age: Int
        val email: String?
    }
}