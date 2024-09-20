package org.example.store.exception.order;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super(String.format(
                "Order with id = %d not found in database",
                id
        ));
    }

}
