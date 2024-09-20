package org.example.store.dto.user;

import java.math.BigDecimal;

public record UserTransactionStats(BigDecimal spent,
                                   Double spentChange,
                                   BigDecimal income,
                                   Double incomeChange,
                                   Long amountOfTransactions,
                                   Long amountOfTransactionsChange) {

}
