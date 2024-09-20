package org.example.store.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorData<T> {

    public String error;
    public List<T> message;
    public LocalDateTime timeStamp;

    public ErrorData(String error, List<T> message, LocalDateTime timeStamp) {
        this.error = error;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}