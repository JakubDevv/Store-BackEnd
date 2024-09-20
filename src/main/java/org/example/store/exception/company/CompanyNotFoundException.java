package org.example.store.exception.company;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(Long companyId) {
        super(String.format(
                "Company with id = %s not found in database",
                companyId
        ));
    }
}
