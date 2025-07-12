package com.runninglane.dto.buddy.test.java.cases.notnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DtoForCaseInterfaceAndGetter {
    @NotNull Dummy getNotNullable();
    @Nullable Dummy getNullable();
}