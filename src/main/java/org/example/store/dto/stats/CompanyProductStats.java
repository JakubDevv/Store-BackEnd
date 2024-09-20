package org.example.store.dto.stats;

import org.example.store.dto.category.CategoryUserExpenseDTO;

import java.math.BigDecimal;
import java.util.List;

public record CompanyProductStats(BigDecimal avgMonthlyIncome,
                                  int activeProducts,
                                  int retiredProducts,
                                  int noOfClients,
                                  double overallRating,
                                  BigDecimal valueOfProducts,
                                  List<CategoryUserExpenseDTO> expenseCategoryList) {

}
