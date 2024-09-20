package org.example.store.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemAdminDTO2(String title,
                                 String size,
                                 BigDecimal price,
                                 int quantity,
                                 Long id,
                                 Long orderItemId,
                                 List<String> parameterValues) {

}
