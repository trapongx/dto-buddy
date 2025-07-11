package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.bytecode.compile.CompileKotlinByteCodeStrategy

open class DefaultByteCodeStrategy : ByteCodeStrategy by CompileKotlinByteCodeStrategy()