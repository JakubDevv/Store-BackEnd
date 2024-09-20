package org.example.store.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderUserDTO(Long id,
                           @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime sendTime,
                           int quantity,
                           BigDecimal price,
                           List<OrderItemUserDTO> items,
                           @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime sentDate,
                           @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime completeDate,
                           String city,
                           String street,
                           int house_number,
                           String zipcode,
                           int phone) {

}
