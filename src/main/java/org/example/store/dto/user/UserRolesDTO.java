package org.example.store.dto.user;

import java.util.List;

public record UserRolesDTO(Long id,
                           List<String> roles) {

}
