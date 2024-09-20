package org.example.store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "productreview")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private int rating;

    private LocalDateTime sendtime;

    public ProductReview() {
    }

    public ProductReview(String message, int rating) {
        this.message = message;
        this.rating = rating;
        this.sendtime = LocalDateTime.now();
    }

}
