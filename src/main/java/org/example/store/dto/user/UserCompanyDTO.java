package org.example.store.dto.user;

import java.math.BigDecimal;

public record UserCompanyDTO(Long id,
                             boolean photo,
                             String firstName,
                             String lastName,
                             BigDecimal money) {
}
