package org.example.store.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format(
                "User with id = %d not found in database",
                id
        ));
    }

    public UserNotFoundException(String username) {
        super(String.format(
                "User with username = %s not found in database",
                username
        ));
    }
}
