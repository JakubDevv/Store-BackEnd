package org.example.store.dto.stats;

import java.math.BigDecimal;

public record CompanyStats(int orders,
                           BigDecimal income,
                           int soldProducts,
                           int comments,
                           int commentsChange,
                           int soldProductsChange,
                           double incomeChange,
                           int ordersChange) {

}
