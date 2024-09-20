package org.example.store.exception.product;

public class SizeNotFoundException extends RuntimeException {

    public SizeNotFoundException(Long id) {
        super(String.format(
                "Size with id = %d not found in database",
                id
        ));
    }
}