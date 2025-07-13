package com.runninglane.dto.buddy.test.java.cases.notnull;

import org.jetbrains.annotations.NotNull;

public interface DtoForCaseInterfaceAndGetter {
    @NotNull @javax.validation.constraints.NotNull Dummy getNotNullable();
    Dummy getNullable();
}