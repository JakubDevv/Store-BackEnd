package org.example.store.dto.order;

import org.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCompanyUpdateDTO(Long userId,
                                    Long orderId,
                                    BigDecimal orderPrice,
                                    LocalDateTime date,
                                    String userName,
                                    Status statusTo) {
}
