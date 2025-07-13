package com.runninglane.dto.buddy.test.java.cases.notnull;

import javax.validation.constraints.NotNull;

public interface DtoForCaseInterfaceAndGetter {
    @NotNull Dummy getNotNullable();
    Dummy getNullable();
}