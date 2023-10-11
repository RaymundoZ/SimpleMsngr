package com.raymundo.simplemsngr.util;

import com.raymundo.simplemsngr.dto.basic.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST.value(),
                e.getClass().getSimpleName(), e.getMessage(), LocalTime.now());
        return ResponseEntity.badRequest().body(errorDto);
    }

    public static String handleValidationResults(BindingResult validationResult) {
        StringBuilder message = new StringBuilder("Validation errors:");
        validationResult.getFieldErrors().forEach(error ->
                message.append(" \n").append(error.getField()).append(": ")
                        .append(error.getDefaultMessage()));
        return message.toString();
    }
}
