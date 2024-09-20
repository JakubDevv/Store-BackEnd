package org.example.store.dto.order;

import org.example.store.model.Status;

import java.time.LocalDateTime;

public record OrderStatusDTO(Status status,
                             LocalDateTime time) {

}
