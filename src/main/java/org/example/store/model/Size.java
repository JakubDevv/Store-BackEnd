package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sizevalue;

    private int quantity;

    public Size() {
    }

    public Size(Long id, String sizevalue, int quantity) {
        this.id = id;
        this.sizevalue = sizevalue;
        this.quantity = quantity;
    }
}
