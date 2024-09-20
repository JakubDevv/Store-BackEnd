package org.example.store.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompanyLongDTO(Long companyId,
                             String companyName,
                             String fullName,
                             int sales,
                             BigDecimal moneyTurnover,
                             int products,
                             int ordersCompleted,
                             int inProgressOrders,
                             LocalDateTime lastActivity,
                             LocalDateTime created,
                             LocalDateTime banned) {

}
