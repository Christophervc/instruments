package com.aplication.rest.instruments.order.dto;

import com.aplication.rest.instruments.order.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderSearchCriteria(OrderStatus status,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate,
                                  String customerDni) {
}
