package com.aplication.rest.instruments.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDTO {
    private UUID id;
    private LocalDateTime date;
    private BigDecimal total;
    private List<OrderItemDTO> items;

}
