package org.example.store.dto.user;

import org.example.store.dto.order.OrderItemAdminDTO2;
import org.example.store.dto.order.OrderStatusDTO;

import java.util.List;

public record CompanyItemsDTO(List<OrderStatusDTO> statuses,
                              String companyName,
                              List<OrderItemAdminDTO2> items) {

}
