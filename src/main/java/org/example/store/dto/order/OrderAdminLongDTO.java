package org.example.store.dto.order;

import org.example.store.dto.user.CompanyItemsDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderAdminLongDTO(Long id,
                                List<OrderStatusDTO> statuses,
                                BigDecimal price,
                                Long userid,
                                String firstName,
                                String lastName,
                                String username,
                                LocalDateTime sendTime,
                                String city,
                                String street,
                                int houseNumber,
                                String zipcode,
                                int phone,
                                List<CompanyItemsDTO> items,
                                int amountOfProducts) {

}
