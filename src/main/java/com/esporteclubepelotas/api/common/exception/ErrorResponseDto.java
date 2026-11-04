package com.esporteclubepelotas.api.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    private int statusCode;
    private String message;
    private String error;
    private String timestamp;
    private String path;
    private Map<String, String> errors;

    public static ErrorResponseDto of(int statusCode, String message, String error, String path) {
        return ErrorResponseDto.builder()
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .timestamp(Instant.now().toString())
                .path(path)
                .build();
    }
}

