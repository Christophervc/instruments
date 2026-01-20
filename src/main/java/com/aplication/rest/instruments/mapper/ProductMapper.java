package com.aplication.rest.instruments.mapper;
import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.entities.Product;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDTO(ProductDTO productDTO, @MappingTarget Product product);

    //List<Product> toEntityList(List<ProductDTO> productDTOs);
}
