package com.aplication.rest.instruments.core.exceptions;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException{
    private final List<ApiError> errors;
    //Constructor for multiple errors fields in validation @Valid
    public ValidationException(List<ApiError> errors) {
        super("Validation failed");
        this.errors = errors;
    }
    //Constructor for simple error field in service
    public ValidationException(String message) {
        super(message);
        this.errors = List.of(new ApiError("BUSINESS_RULE_VIOLATION", message));
    }
}
