package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "fk_transaction_order")
    private Order order;

    private LocalDateTime date;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    public Transaction(BigDecimal amount, Type type) {
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.type = type;
    }

    public Transaction() {
    }

}
