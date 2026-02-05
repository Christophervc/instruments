package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.order.dto.OrderRequest;

public interface IOrderService {
    Result<Order> createOrder(OrderRequest request);
}
