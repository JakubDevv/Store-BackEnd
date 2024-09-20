package org.example.store.exception.filter;

public class FilterNotFoundException extends RuntimeException {

    public FilterNotFoundException(Long id) {
        super(String.format(
                "Filter with id = %d not found in database",
                id
        ));
    }
}