package org.example.store.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OrderUserDTO2(Long id,
                            LocalDateTime date,
                            BigDecimal price,
                            Set<String> companies,
                            Set<Long> companiesId,
                            String status,
                            String country,
                            String city,
                            String street,
                            Integer num,
                            String zipCode,
                            LocalDateTime send,
                            Set<Long> productsId,
                            List<OrderItemUserDTO> items,
                            LocalDateTime dateReceipt,
                            int phone) {


}
