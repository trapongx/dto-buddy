package com.runninglane.dto.buddy.test.java.cases.bytecode.codegen.tools;

import com.runninglane.dto.buddy.javainterop.bytecode.codegen.tools.StaticJavaCodeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticJavaCodeGenGeneratorTest {
    public interface Dto1 {
        String getName();
    }

    public interface Dto2 {
        String getName();
    }

    @Test
    public void testGenerateStaticCode() throws Exception
    {
        var outputPath = Files.createTempDirectory("static-java-codegen-test");
        try {
            var tool = new StaticJavaCodeGenerator(outputPath.toFile());

            tool.generate(List.of(Dto1.class, Dto2 .class));

            var allFilesGenerated = Files.walk(outputPath)
                .filter(Files::isRegularFile)
                .map(path -> path.toString().substring(outputPath.toString().length()+1))
            .sorted()
                .collect(Collectors.toList());

            assertEquals(2, allFilesGenerated.size());
            assertEquals(
                List.of(
                    "com/runninglane/dto/buddy/test/java/cases/bytecode/codegen/tools/StaticJavaCodeGenGeneratorTest$Dto1$Dto.kt",
                    "com/runninglane/dto/buddy/test/java/cases/bytecode/codegen/tools/StaticJavaCodeGenGeneratorTest$Dto2$Dto.kt"
                ),
                allFilesGenerated
            );
        } finally {
            deleteRecursively(outputPath.toFile());
        }
    }

    private void deleteRecursively(File file)
    {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            file.deleteOnExit();
        }
    }
}