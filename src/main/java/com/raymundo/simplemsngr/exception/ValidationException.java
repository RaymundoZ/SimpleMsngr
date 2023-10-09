package com.raymundo.simplemsngr.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationException extends Exception {

    private final List<String> messages;
    private final HttpStatusCode status;

    public ValidationException() {
        messages = new ArrayList<>();
        status = HttpStatus.BAD_REQUEST;
    }

    public ValidationException(String message) {
        this();
        messages.add(message);
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
