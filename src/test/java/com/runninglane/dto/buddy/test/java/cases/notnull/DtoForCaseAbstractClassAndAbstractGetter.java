package com.runninglane.dto.buddy.test.java.cases.notnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DtoForCaseAbstractClassAndAbstractGetter {
    public abstract @NotNull Dummy getNotNullable();
    public abstract @Nullable Dummy getNullable();
}