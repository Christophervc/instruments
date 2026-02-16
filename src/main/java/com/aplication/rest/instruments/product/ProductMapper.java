package com.aplication.rest.instruments.product;
import com.aplication.rest.instruments.manufacturer.ManufacturerMapper;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ManufacturerMapper.class}) // annotation for dependency injection
public interface ProductMapper {

    @Mapping(target = "manufacturer", source = "manufacturer", qualifiedByName = "toSummaryDTO")
    ProductDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "manufacturer", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDTO(ProductDTO productDTO, @MappingTarget Product product);

}
