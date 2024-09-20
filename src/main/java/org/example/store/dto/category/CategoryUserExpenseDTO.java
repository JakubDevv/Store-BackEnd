package org.example.store.dto.category;

import java.math.BigDecimal;

public record CategoryUserExpenseDTO(String category,
                                     BigDecimal amount,
                                     double percentage) {

}
