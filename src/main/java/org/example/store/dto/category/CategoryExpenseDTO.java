package org.example.store.dto.category;

import org.example.store.dto.product.ProductExpenseDTO;

import java.math.BigDecimal;
import java.util.List;

public record CategoryExpenseDTO(Long id,
                                 String name,
                                 BigDecimal price,
                                 List<ProductExpenseDTO> products) {
}
