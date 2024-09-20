package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "maincategory")
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_subcategory_maincategory")
    private List<SubCategory> subCategories;

    private LocalDateTime deleted;

    public MainCategory(String name) {
        this.name = name;
    }

    public MainCategory() {
    }

}
