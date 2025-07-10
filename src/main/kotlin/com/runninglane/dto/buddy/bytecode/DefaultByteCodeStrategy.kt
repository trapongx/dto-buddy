package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.bytecode.k2jvm.EmbeddedCompilerByteCodeStrategy

open class DefaultByteCodeStrategy : ByteCodeStrategy by EmbeddedCompilerByteCodeStrategy()