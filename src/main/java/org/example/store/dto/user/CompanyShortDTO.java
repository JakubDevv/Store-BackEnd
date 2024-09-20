package org.example.store.dto.user;

import java.time.LocalDateTime;

public record CompanyShortDTO(Long id,
                              String name,
                              LocalDateTime created,
                              LocalDateTime banned) {

}
