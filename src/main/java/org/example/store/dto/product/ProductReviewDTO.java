package org.example.store.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ProductReviewDTO(Long id,
                               String message,
                               int rating,
                               String userName,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime sendTime) {

}
