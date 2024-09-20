package org.example.store.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemCreateDTO(@Min(1) Long id,
                                 @Min(1) int quantity,
                                 @NotNull String name,
                                 @Min(1) BigDecimal price,
                                 String sizeId,
                                 @NotNull String size) {

}
