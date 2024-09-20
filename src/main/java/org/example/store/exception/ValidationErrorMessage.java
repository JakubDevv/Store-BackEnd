package org.example.store.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorMessage extends ErrorMessage {

    public List<String> details;

    public ValidationErrorMessage(String error, String message, LocalDateTime timeStamp, List<String> details) {
        super(error, message, timeStamp);
        this.details = details;
    }
}