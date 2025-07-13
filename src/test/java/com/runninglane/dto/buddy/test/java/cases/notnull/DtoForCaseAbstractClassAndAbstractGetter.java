package com.runninglane.dto.buddy.test.java.cases.notnull;

import javax.validation.constraints.NotNull;

public abstract class DtoForCaseAbstractClassAndAbstractGetter {
    public abstract @NotNull Dummy getNotNullable();
    public abstract Dummy getNullable();
}