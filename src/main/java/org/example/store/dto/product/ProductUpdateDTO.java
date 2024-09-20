package org.example.store.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.store.dto.parameter.ParameterDTO;
import org.example.store.dto.size.SizeDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateDTO(@Min(1) Long id,
                               @NotBlank String title,
                               @NotBlank @Size(max = 255) String description,
                               @Positive BigDecimal price,
                               @Positive BigDecimal discountPrice,
                               List<ParameterDTO> parameters,
                               List<SizeDTO> sizes) {

}
