package com.aplication.rest.instruments.core.exceptions;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result<?>> handleNotFoundException(NotFoundException exception) {
        ApiError error = new ApiError("NOT_FOUND", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.isFailure(error));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<?>> handleValidation(ValidationException exception){
        ApiError error = new ApiError("VALIDATION_ERROR", exception.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Result.isFailure(exception.getErrors().toArray(new ApiError[0])));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleInternalServerException(Exception exception){
        ApiError error = new ApiError("INTERNAL_SERVER_ERROR", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.isFailure(error));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleMethodArgumentNotValidEx(MethodArgumentNotValidException exception){
        List<ApiError> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error-> {
                    if (error instanceof FieldError fieldError){
                        return new ApiError("FIELD_ERRORS", fieldError.getField() + ": "+fieldError.getDefaultMessage());
                    } else {
                        return new ApiError("GLOBAL_VALIDATION_ERROR", error.getDefaultMessage());
                    }
                })
                .toList();
        return ResponseEntity.badRequest().body(Result.isFailure(errors.toArray(new ApiError[0])));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<?>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String message = exception.getMostSpecificCause().getMessage();
        String userMessage = "DATA INTEGRITY ERROR";

        if (message != null) {
            if (message.contains("uk_product_sku")) {
                userMessage = "SKU already exists";
            } else if (message.contains("uk_product_slug")) {
                userMessage = "There is already a product with that name (duplicate slug)";
            } else if (message.contains("unique")) {
                userMessage = "duplicate entry: one of the unique fields already exists";
            }
        }
        ApiError error = new ApiError("DUPLICATE ENTRY", userMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.isFailure(error));
    }


}
