package org.example.store.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.store.dto.parameter.ParameterDTO;
import org.example.store.dto.size.SizeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDTO2(Long id,
                          String title,
                          String description,
                          BigDecimal price,
                          BigDecimal discountPrice,
                          @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime addTime,
                          int sales,
                          List<ParameterDTO> parameters,
                          List<SizeDTO> sizes,
                          int images,
                          LocalDateTime suspended,
                          int numOfClients,
                          double rating,
                          int numOfComments,
                          BigDecimal valOfStoredProducts,
                          String category) {

}

