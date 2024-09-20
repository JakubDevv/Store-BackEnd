package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "orderitem")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_order_status_orderitem")
    private List<OrderStatus> statuses;

    private BigDecimal price;

    private String size;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "fk_product_orderitem")
    private Product product;

    public OrderItem() {
    }

    public OrderItem(int quantity) {
        this.quantity = quantity;
    }

}
