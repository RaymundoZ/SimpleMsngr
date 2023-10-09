package com.raymundo.simplemsngr.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.raymundo.simplemsngr.util.BaseDto;

import java.time.LocalTime;
import java.util.List;

public record ExceptionResponse(

        List<String> messages,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime timestamp

) implements BaseDto {
}
