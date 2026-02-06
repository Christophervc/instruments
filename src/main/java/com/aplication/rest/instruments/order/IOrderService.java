package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.dto.OrderSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IOrderService {
    Result<OrderDTO> createOrder(OrderRequest request);
    Result<OrderDTO> cancelOrder(UUID uuid);
    Result<OrderDTO> findById(UUID id);
    Result<Page<OrderDTO>> findAll(Pageable pageable, OrderSearchCriteria criteria);
}
