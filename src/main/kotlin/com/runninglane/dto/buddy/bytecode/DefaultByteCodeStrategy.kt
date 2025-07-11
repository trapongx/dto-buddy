package com.runninglane.dto.buddy.bytecode

import com.runninglane.dto.buddy.bytecode.compile.CompileKotlinByteCodeStrategyCompliment

open class DefaultByteCodeStrategy : ByteCodeStrategy by ThreeStepsByteCodeStrategy(
    CompileKotlinByteCodeStrategyCompliment()
)