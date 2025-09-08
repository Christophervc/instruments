package com.aplication.rest.instruments.core.exceptions;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException{
    private final List<ApiError> errors;
    public ValidationException(List<ApiError> errors) {
        this.errors = errors;
    }
}
