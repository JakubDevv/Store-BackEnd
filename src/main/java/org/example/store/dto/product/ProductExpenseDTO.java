package org.example.store.dto.product;

import java.math.BigDecimal;

public record ProductExpenseDTO(Long id,
                                String name,
                                int amount) {
}
