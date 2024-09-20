package org.example.store.dto.product;

import org.example.store.dto.order.OrderItemAdminDTO;
import org.example.store.dto.size.SizeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductAdminStatsDTO(Long id,
                                   String title,
                                   String description,
                                   String subCategory,
                                   BigDecimal price,
                                   BigDecimal discount_price,
                                   String companyName,
                                   LocalDateTime addTime,
                                   BigDecimal moneyTurnover,
                                   int sales,
                                   LocalDateTime deleted,
                                   List<OrderItemAdminDTO> orders,
                                   List<String> images,
                                   List<SizeDTO> sizes,
                                   List<String> types) {

}
