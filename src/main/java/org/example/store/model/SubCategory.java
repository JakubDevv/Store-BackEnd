package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "subcategory")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_product_subcategory")
    private List<Product> products;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_filter_subcategory")
    private List<Filter> filters;

    private LocalDateTime deleted;

    public SubCategory(String name) {
        this.name = name;
    }

    public SubCategory() {
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

}
