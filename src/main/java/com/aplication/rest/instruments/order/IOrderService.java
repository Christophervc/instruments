package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderRequest;

public interface IOrderService {
    Result<OrderDTO> createOrder(OrderRequest request);
}
