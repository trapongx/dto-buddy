package com.runninglane.dto.buddy.test.java.cases.notnull;

import org.jetbrains.annotations.NotNull;

public abstract class DtoForCaseAbstractClassAndAbstractGetter {
    public abstract @NotNull @javax.validation.constraints.NotNull Dummy getNotNullable();
    public abstract Dummy getNullable();
}