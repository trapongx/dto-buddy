package com.runninglane.dto.buddy.javainterop.bytecode

import com.runninglane.dto.buddy.bytecode.ByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.PropertyDescriptor
import com.runninglane.dto.buddy.bytecode.codegen.CodeCompiler
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenBasedByteCodeStrategy
import com.runninglane.dto.buddy.bytecode.codegen.CodeGenerator

typealias KByteCodeStrategy = ByteCodeStrategy
typealias KPropertyDescriptor = PropertyDescriptor
typealias KCodeGenBasedByteCodeStrategy<B> = CodeGenBasedByteCodeStrategy<B>
typealias KCodeGenerator<B> = CodeGenerator<B>
typealias KCodeCompiler = CodeCompiler
