package org.example.store.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProductReviewCreateDTO(@Size(max = 255) String message,
                                     @Min(1) int rating,
                                     @Min(1) Long productId) {

}
