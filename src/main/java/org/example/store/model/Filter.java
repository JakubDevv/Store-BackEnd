package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "filter")
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_filtervalue_filter")
    private List<FilterValue> values;

    public Filter() {
    }

    public Filter(String key) {
        this.key = key;
    }
}
