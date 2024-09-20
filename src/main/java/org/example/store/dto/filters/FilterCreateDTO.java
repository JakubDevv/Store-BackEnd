package org.example.store.dto.filters;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FilterCreateDTO (@Min(1) Long subCategoryId,
                               @NotNull String name) {
}
