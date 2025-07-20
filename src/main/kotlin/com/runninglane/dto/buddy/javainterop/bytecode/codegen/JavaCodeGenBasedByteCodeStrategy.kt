package com.runninglane.dto.buddy.javainterop.bytecode.codegen

import com.runninglane.dto.buddy.javainterop.bytecode.CodeGenBasedByteCodeStrategy
import com.squareup.javapoet.TypeSpec

class JavaCodeGenBasedByteCodeStrategy(
    generator: JavaCodeGenerator,
    compiler: JavaCodeCompiler
) : CodeGenBasedByteCodeStrategy<TypeSpec.Builder>(generator, compiler) {
    constructor(generator: JavaCodeGenerator) : this(generator, JavaCodeCompiler())
    constructor(compiler: JavaCodeCompiler) : this(JavaCodeGenerator(), compiler)
    constructor() : this(JavaCodeGenerator(), JavaCodeCompiler())
}