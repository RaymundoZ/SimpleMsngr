package com.raymundo.simplemsngr.exception;

import com.raymundo.simplemsngr.dto.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleValidationException(ValidationException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessages(), LocalTime.now());
        return ResponseEntity.status(e.getStatus()).body(response);
    }
}
