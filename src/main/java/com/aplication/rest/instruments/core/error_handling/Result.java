package com.aplication.rest.instruments.core.error_handling;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
// Force Jackson saving "@class": "com...Result".
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record Result<T>(boolean isSuccess, T data, List<ApiError> errors) {
    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, List.of());
    }

    public static <T> Result<T> isFailure(ApiError... errors) {
        return new Result<>(false, null, List.of(errors));
    }
}
