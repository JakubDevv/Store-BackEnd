package org.example.store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discount_price;

    private int sales;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_image_product")
    private List<Image> images;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_size_product")
    private List<Size> sizes;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_review_product")
    private List<ProductReview> reviews;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_parameter_product")
    private List<Parameter> parameters;

    private LocalDateTime created;

    private LocalDateTime retired;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_product_orderitem")
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "fk_product_company")
    private Company company;

    public Product(String name, String description, BigDecimal price, List<Size> sizes, List<Parameter> parameters) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sizes = sizes;
        this.parameters = parameters;
        this.created = LocalDateTime.now();
    }

    public Product() {
    }

    public void addImage(Image image) {
        this.images.add(image);
    }
}
