package com.runninglane.dto.buddy.exception

class DtoBuddySystemException : DtoBuddyException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}