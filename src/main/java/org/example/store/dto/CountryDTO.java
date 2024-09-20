package org.example.store.dto;

import java.math.BigDecimal;

public record CountryDTO(String name,
                         BigDecimal money,
                         Long amountOfOrders) {
}
