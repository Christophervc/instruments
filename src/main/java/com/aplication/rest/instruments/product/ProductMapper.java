package com.aplication.rest.instruments.product;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring") // annotation for dependency injection
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDTO(ProductDTO productDTO, @MappingTarget Product product);

}
