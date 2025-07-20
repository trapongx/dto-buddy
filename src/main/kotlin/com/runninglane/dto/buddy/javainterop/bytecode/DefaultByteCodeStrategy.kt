package com.runninglane.dto.buddy.javainterop.bytecode

import com.runninglane.dto.buddy.javainterop.bytecode.codegen.JavaCodeGenBasedByteCodeStrategy

open class DefaultByteCodeStrategy : ByteCodeStrategy by JavaCodeGenBasedByteCodeStrategy()