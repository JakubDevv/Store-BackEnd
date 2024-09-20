package org.example.store.exception.user;

import java.math.BigDecimal;

public class MoneyNotEnoughException extends RuntimeException {

    public MoneyNotEnoughException(BigDecimal money) {
        super(String.format(
                "User doesn't have %s",
                money
        ));
    }

    public MoneyNotEnoughException() {
        super("User doesn't have enough money");
    }
}
