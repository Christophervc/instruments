package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customer.id", source = "user.id")
    @Mapping(target = "customer.firstName", source = "user.firstName")
    @Mapping(target = "customer.lastName", source = "user.lastName")
    @Mapping(target = "customer.email", source = "user.email")
    @Mapping(target = "customer.dni", source = "user.dni")
    @Mapping(target = "items", source = "items")
    OrderDTO toDTO(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "subtotal", expression = "java(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderItemDTO toDTO(OrderItem item);
    /*
        @Mapping(target = "items", source = "items")
        OrderDTO toDTO(Order order);
        @Mapping(target = "productId", source = "product.id")

        @Mapping(target = "subtotal", expression = "java(calculateSubtotal(item))")
        OrderItemDTO toDTO(OrderItem item);

    default BigDecimal calculateSubtotal(OrderItem item) {
        return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
    }
*/
}
