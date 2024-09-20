package org.example.store.dto.filters;

import java.util.List;

public record FilterDTO(Long id,
                        String key,
                        List<FilterValueDTO> values) {

}
