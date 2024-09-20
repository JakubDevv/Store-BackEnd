package org.example.store.exception.subcategory;

public class SubcategoryNotFoundException extends RuntimeException {

    public SubcategoryNotFoundException(Long id) {
        super(String.format(
                "Subcategory with id = %d not found in database",
                id
        ));
    }

    public SubcategoryNotFoundException() {
        super("Subcategory not found in the database");
    }
}
