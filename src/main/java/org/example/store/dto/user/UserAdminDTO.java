package org.example.store.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserAdminDTO(Long id,
                           String username,
                           String first_name,
                           String last_name,
                           LocalDateTime created,
                           LocalDateTime banned,
                           String company_name,
                           BigDecimal moneyTurnover,
                           int allOrders,
                           int sentOrders,
                           BigDecimal balance,
                           boolean photo) {

}
