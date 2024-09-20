package org.example.store.dto.transaction;

import org.example.store.model.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record TransactionDTO(Long id,
                            BigDecimal amount,
                            Long orderId,
                            LocalDateTime date,
                            Type type,
                            Set<String> names,
                            List<String> products) {


}
