package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.KotlinCodeGenerator

open class DefaultByteCodeStrategy : ByteCodeStrategy by CodeGenBasedByteCodeStrategy(
    KotlinCodeGenerator(), KotlinCodeCompiler()
)