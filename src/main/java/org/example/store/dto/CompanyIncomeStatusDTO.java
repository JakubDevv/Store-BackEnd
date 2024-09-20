package org.example.store.dto;

import org.example.store.model.Status;

import java.math.BigDecimal;

public record CompanyIncomeStatusDTO(Status status, BigDecimal income) {
}
