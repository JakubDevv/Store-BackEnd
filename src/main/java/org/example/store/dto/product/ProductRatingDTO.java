package org.example.store.dto.product;

import lombok.Builder;

@Builder
public record ProductRatingDTO(double average,
                               long amount,
                               long fiveStar,
                               long fourStar,
                               long threeStar,
                               long twoStar,
                               long oneStar) {

}
