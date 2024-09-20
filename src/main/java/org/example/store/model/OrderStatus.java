package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_status")
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private LocalDateTime time;

    public OrderStatus() {
    }

    public OrderStatus(Status status) {
        this.status = status;
        this.time = LocalDateTime.now();
    }

}
