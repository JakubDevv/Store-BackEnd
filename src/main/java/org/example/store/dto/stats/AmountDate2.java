package org.example.store.dto.stats;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AmountDate2(LocalDateTime date,
                          BigDecimal amount) {

}
