package com.runninglane.dto.buddy.javainterop.bytecode

import com.runninglane.dto.buddy.javainterop.bytecode.compile.CompileJavaByteCodeStrategyCompliment

open class DefaultByteCodeStrategy : ByteCodeStrategy by ThreeStepsByteCodeStrategy(
    CompileJavaByteCodeStrategyCompliment()
)