package org.example.store.dto.stats;

import org.example.store.dto.category.CategoryUserExpenseDTO;

import java.math.BigDecimal;
import java.util.List;

public record UserSpendingStats(List<AmountDate2> spending,
                                List<AmountDate2> expenses,
                                BigDecimal avgMonthlySpend,
                                BigDecimal last6MonthTotal,
                                BigDecimal lastYearTotal,
                                List<CategoryUserExpenseDTO> expenseByCategory) {

}
