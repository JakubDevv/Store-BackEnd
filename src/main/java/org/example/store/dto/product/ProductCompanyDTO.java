package org.example.store.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductCompanyDTO(Long id,
                                LocalDateTime date,
                                String name,
                                String category,
                                boolean categoryActive,
                                int sales,
                                int available,
                                BigDecimal price,
                                BigDecimal discount_price,
                                BigDecimal income,
                                boolean active) {

}
