package com.aplication.rest.instruments.order.dto;

import com.aplication.rest.instruments.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {
}
