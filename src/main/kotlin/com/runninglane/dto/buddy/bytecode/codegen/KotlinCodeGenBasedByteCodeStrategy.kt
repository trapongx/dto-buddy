package com.runninglane.dto.buddy.bytecode.codegen

import com.squareup.kotlinpoet.TypeSpec

class KotlinCodeGenBasedByteCodeStrategy(
    generator: KotlinCodeGenerator = KotlinCodeGenerator(),
    compiler: KotlinCodeCompiler = KotlinCodeCompiler()
) : CodeGenBasedByteCodeStrategy<TypeSpec.Builder>(generator, compiler)