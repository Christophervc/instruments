package com.aplication.rest.instruments.manufacturer;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.product.ProductMapper;
import org.mapstruct.*;
import java.util.List;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ManufacturerMapper {

    ManufacturerDTO toDTO(Manufacturer manufacturer);
    @Named("toSummaryDTO")
    @Mapping(target = "productList", ignore = true)
    ManufacturerDTO toSummaryDTO(Manufacturer manufacturer);

    @Mapping(target = "id", ignore = true)
    Manufacturer toEntity(ManufacturerDTO manufacturerDTO);

    List<ManufacturerDTO> toDTOList(List<Manufacturer> manufacturers);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateManufacturerFromDto(ManufacturerDTO dto, @MappingTarget Manufacturer manufacturer);
}
