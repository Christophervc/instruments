package com.aplication.rest.instruments.order.enums;

import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public enum OrderStatus {
    PENDING,
    PAID,
    DELIVERED,
    CANCELLED
}
