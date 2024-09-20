package org.example.store.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemUserDTO(Long id,
                               String title,
                               String size,
                               BigDecimal price,
                               int quantity,
                               Long productId,
                               String companyName,
                               List<String> parameters) {

}
