package org.example.store.dto.product;


import java.io.Serializable;
import java.math.BigDecimal;

public record ProductDTO4(Long id,
                          String title,
                          String description,
                          BigDecimal price,
                          BigDecimal discountPrice,
                          int starsRate,
                          int amountOfImages) implements Serializable {

}
