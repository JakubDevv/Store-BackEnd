package org.example.store.dto.size;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record SizeDTO(Long id,
                      String sizeValue,
                      int quantity) implements Serializable {

}
