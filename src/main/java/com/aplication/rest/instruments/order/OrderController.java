package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.dto.OrderSearchCriteria;
import com.aplication.rest.instruments.order.dto.UpdateOrderStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping()
    @Operation(summary = "Get all orders history", description = "filter between dates and order state")
    public ResponseEntity<Result<Page<OrderDTO>>> findAll(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @ParameterObject OrderSearchCriteria criteria) {
        return ResponseEntity.ok(orderService.findAll(pageable, criteria));
    }

    @Operation(summary = "Get an order details", description = "Get an order details, requires CUSTOMER, ADMIN or STAFF role")
    @GetMapping("/{id}")
    public ResponseEntity<Result<OrderDTO>> orderDetails(@PathVariable UUID id) {
        Result<OrderDTO> result = orderService.findById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create a new order", description = "Create a new order, requires CUSTOMER, ADMIN or STAFF role")
    @PostMapping
    public ResponseEntity<Result<OrderDTO>> createOrder(@Valid @RequestBody OrderRequest request) throws URISyntaxException {
        Result<OrderDTO> result = orderService.createOrder(request);
        return ResponseEntity.created(new URI("api/v1/orders" + result.data().id())).body(result);
    }

    @Operation(summary = "Cancel an order", description = "Cancel an order, requires ADMIN or STAFF role")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Result<OrderDTO>> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @Operation(summary = "Update an order status", description = "Update an order status, requires ADMIN or STAFF role")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Result<OrderDTO>> updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.status()));
    }

}
