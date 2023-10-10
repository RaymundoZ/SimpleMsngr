package com.raymundo.simplemsngr.dto.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public record ErrorDto(

        @JsonProperty(value = "status_code")
        Integer statusCode,

        String exception,

        String message,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime timestamp

) {
}
