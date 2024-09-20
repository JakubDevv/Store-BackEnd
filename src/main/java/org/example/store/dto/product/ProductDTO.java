package org.example.store.dto.product;

import lombok.Builder;
import org.example.store.dto.parameter.ParameterDTO2;
import org.example.store.dto.size.SizeDTO;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductDTO(Long id,
                         String name,
                         String description,
                         BigDecimal price,
                         BigDecimal discountPrice,
                         String companyName,
                         List<SizeDTO> sizes,
                         List<ParameterDTO2> parameters,
                         int images) {

}
