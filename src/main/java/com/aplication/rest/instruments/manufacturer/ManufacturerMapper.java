package com.aplication.rest.instruments.manufacturer;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import org.mapstruct.*;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ManufacturerMapper {

    ManufacturerDTO toDTO(Manufacturer manufacturer);
    @Named("toSummaryDTO")
    ManufacturerDTO toSummaryDTO(Manufacturer manufacturer);

    @Mapping(target = "productList", ignore = true) // Ignoramos la lista al crear la entidad
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Manufacturer toEntity(ManufacturerDTO manufacturerDTO);

    @Mapping(target = "productList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateManufacturerFromDto(ManufacturerDTO dto, @MappingTarget Manufacturer manufacturer);
}
