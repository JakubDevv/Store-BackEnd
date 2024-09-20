package org.example.store.dto.order;

import org.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemAdminDTO(Long id,
                                String sizeValue,
                                BigDecimal price,
                                int quantity,
                                LocalDateTime sendTime,
                                Status status) {

}
