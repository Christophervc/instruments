package com.aplication.rest.instruments.core.error_handling;

import com.aplication.rest.instruments.entities.Product;

import java.util.List;

public record Result<T>(boolean isSuccess, T data, List<ApiError> errors) {
    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, List.of());
    }

    public static <T> Result<T> isFailure(ApiError... errors) {
        return new Result<>(false, null, List.of(errors));
    }
}
