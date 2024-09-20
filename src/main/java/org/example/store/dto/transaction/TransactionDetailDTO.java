package org.example.store.dto.transaction;

import java.math.BigDecimal;

public record TransactionDetailDTO(Long userId,
                                   BigDecimal price,
                                   String firstName,
                                   String lastName,
                                   String companyName,
                                   boolean photo) {
}
