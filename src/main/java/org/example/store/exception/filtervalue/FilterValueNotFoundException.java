package org.example.store.exception.filtervalue;

public class FilterValueNotFoundException extends RuntimeException {

    public FilterValueNotFoundException(Long id) {
        super(String.format(
                "FilterValue with id = %d not found in database",
                id
        ));
    }
}