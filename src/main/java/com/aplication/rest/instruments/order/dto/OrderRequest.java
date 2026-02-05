package com.aplication.rest.instruments.order.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest (@NotNull List<OrderItemRequest> products) {}

