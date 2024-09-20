package org.example.store.exception.maincategory;

public class MainCategoryNotFoundException extends RuntimeException {

    public MainCategoryNotFoundException(Long id) {
        super(String.format(
                "Category with id = %d not found in database",
                id
        ));
    }
}