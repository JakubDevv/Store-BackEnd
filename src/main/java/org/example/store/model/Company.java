package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime created;

    private LocalDateTime banned;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_product_company")
    private List<Product> products;

    public Company() {
    }

    public Company(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
    }

}
