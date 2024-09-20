package org.example.store.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserDTO(Long id,
                      String firstName,
                      String lastName,
                      String username,
                      String companyName,
                      LocalDateTime created,
                      LocalDateTime banned,
                      BigDecimal balance,
                      boolean photo) {


}