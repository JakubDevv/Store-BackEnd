package org.example.store.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.store.dto.category.CategoryExpenseDTO;
import org.example.store.dto.product.ProductExpenseDTO;
import org.example.store.model.Product;
import org.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCompanyShortDTO(Long id,
                                   boolean photo,
                                   int quantity,
                                   @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime date,
                                   BigDecimal price,
                                   Status status,
                                   String firstName,
                                   String lastName,
                                   String country,
                                   String city,
                                   String street,
                                   int apartment_num,
                                   int items,
                                   Long userId,
                                   List<CategoryExpenseDTO> categories
                                   ) {

}
