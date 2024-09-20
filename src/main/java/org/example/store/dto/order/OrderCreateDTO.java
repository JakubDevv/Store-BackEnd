package org.example.store.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record OrderCreateDTO(@NotBlank String country,
                             @NotBlank String city,
                             @NotBlank String street,
                             @Min(1) int addressNumber,
                             @NotBlank String zip_code,
                             int phone,
                             List<OrderItemCreateDTO> items) {

}
