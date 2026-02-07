package com.aplication.rest.instruments.manufacturer;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    ManufacturerDTO toDTO(Manufacturer manufacturer);

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
