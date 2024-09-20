package org.example.store.dto.stats;

import java.math.BigDecimal;


public record AdminStats(int newProducts,
                         int orders,
                         BigDecimal moneyTurnover,
                         int soldProducts) {

}
