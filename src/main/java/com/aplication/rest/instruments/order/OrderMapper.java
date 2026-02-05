package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "items", source = "items")
    OrderDTO toDTO(Order order);
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(item))")
    OrderItemDTO toDTO(OrderItem item);

    default BigDecimal calculateSubtotal(OrderItem item) {
        return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
    }

}
