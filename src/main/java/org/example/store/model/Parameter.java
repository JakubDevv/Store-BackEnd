package org.example.store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "parameter")
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String value;

    public Parameter(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public Parameter() {
    }

    public boolean compareFilters(String key, String values) {
        String[] values2 = values.split(", ");
        for (String value : values2) {
            if (Objects.equals(this.key, key)) {
                if (Objects.equals(this.value, value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
