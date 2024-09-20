package org.example.store.exception.product;

import org.example.store.dto.order.OrderItemCreateDTO;

import java.util.List;

public class ProductPropertiesChanged extends RuntimeException {

    private List<OrderItemCreateDTO> changedProducts;

    public ProductPropertiesChanged(List<OrderItemCreateDTO> products) {
        super("Product properties changed");
        this.changedProducts = products;
    }

    public List<OrderItemCreateDTO> getChangedProducts() {
        return changedProducts;
    }


}