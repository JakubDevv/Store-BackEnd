package org.example.store.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductAdminDTO(Long id,
                              String name,
                              BigDecimal actualPrice,
                              int sales,
                              String companyName,
                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime addTime,
                              LocalDateTime suspended,
                              String subCategory,
                              BigDecimal totalRevenue) {

}
