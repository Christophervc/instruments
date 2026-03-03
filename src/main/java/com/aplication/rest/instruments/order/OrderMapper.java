package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.order.dto.CustomerInfoDTO;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    //in expression use buildCustomerInfo method
    @Mapping(target = "customer", expression = "java(buildCustomerInfo(order))")
    @Mapping(target = "items", source = "items")
    OrderDTO toDTO(Order order);

    //build customer info record
    default CustomerInfoDTO buildCustomerInfo(Order order) {
        if (order == null) {
            return null;
        }

        UUID userId = null;
        Boolean isActive = false;
        //if user status still active (user is not banned), extract data from user entity
        if (order.getUser() != null) {
            userId = order.getUser().getId();
            isActive = order.getUser().getActive();
        }
        // return new customer info record
        return new CustomerInfoDTO(
                userId,
                order.getCustomerFirstName(),
                order.getCustomerLastName(),
                order.getCustomerEmail(),
                order.getCustomerDni(),
                order.getCustomerPhone(),
                isActive
        );
    }

    @Mapping(target = "productId", source = "product.id")
    @Mapping(
            target = "subtotal",
            expression = "java(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))"
    )
    OrderItemDTO toDTO(OrderItem item);
}