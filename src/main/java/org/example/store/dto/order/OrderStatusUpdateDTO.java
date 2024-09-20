package org.example.store.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.store.model.Status;

public record OrderStatusUpdateDTO(@Min(1) Long orderId,
                                   @NotNull Status status) {
}
