package com.runninglane.dto.buddy.test.cases.bytecode.codegen.tools

import com.runninglane.dto.buddy.bytecode.codegen.tools.StaticKotlinCodeGenerator
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertEquals

class StaticKotlinCodeGenGeneratorTest {
    interface Dto1 {
        val name: String
    }

    interface Dto2 {
        val name: String
    }

    @Test
    fun testGenerateStaticCode() {
        val outputPath = Files.createTempDirectory("static-kotlin-codegen-test")
        try {
            val tool = StaticKotlinCodeGenerator(outputPath.toFile())

            tool.generate(listOf(Dto1::class, Dto2::class))

            val allFilesGenerated = Files.walk(outputPath)
                .filter { Files.isRegularFile(it) }
                .map { it.toString().removePrefix("$outputPath/") }
                .sorted()
                .toList()

            assertEquals(2, allFilesGenerated.size)
            assertEquals(
                listOf(
                    "com/runninglane/dto/buddy/test/cases/bytecode/codegen/tools/StaticKotlinCodeGenGeneratorTest\$Dto1\$Dto.kt",
                    "com/runninglane/dto/buddy/test/cases/bytecode/codegen/tools/StaticKotlinCodeGenGeneratorTest\$Dto2\$Dto.kt",
                ),
                allFilesGenerated
            )
        } finally {
            outputPath.toFile().deleteRecursively()
        }
    }
}