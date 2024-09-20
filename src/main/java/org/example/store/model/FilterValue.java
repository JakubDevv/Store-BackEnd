package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "filtervalue")
public class FilterValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String value;

    public FilterValue() {
    }

    public FilterValue(String value) {
        this.value = value;
    }

}
