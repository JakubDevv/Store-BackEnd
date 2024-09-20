package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_orderitem_orders_table")
    private List<OrderItem> orderItem;

    private String country;

    private String city;

    private String street;

    private int house_number;

    private String zipcode;

    private int phone;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_order_status_order")
    private List<OrderStatus> statuses;

    public Order() {
    }

    public Order(String city, String street, int house_number, String zipcode, int phone) {
        this.orderItem = new ArrayList<>();
        this.city = city;
        this.street = street;
        this.house_number = house_number;
        this.zipcode = zipcode;
        this.phone = phone;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItem.add(item);
    }
}
