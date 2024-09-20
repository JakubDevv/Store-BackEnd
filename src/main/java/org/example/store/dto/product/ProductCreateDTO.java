package org.example.store.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.store.dto.parameter.ParameterDTO;
import org.example.store.dto.size.SizeDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateDTO(@NotBlank @Size(max = 255) String title,
                               @NotBlank @Size(max = 255) String description,
                               @Positive BigDecimal price,
                               List<ParameterDTO> parameters,
                               List<SizeDTO> sizes,
                               Long subcategoryId) {

}
