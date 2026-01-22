package com.aplication.rest.instruments.mapper;
import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.entities.Manufacturer;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    ManufacturerDTO toDTO(Manufacturer manufacturer);

    @Mapping(target = "id", ignore = true)
    Manufacturer toEntity(ManufacturerDTO manufacturerDTO);

    List<ManufacturerDTO> toDTOList(List<Manufacturer> manufacturers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateManufacturerFromDto(ManufacturerDTO dto, @MappingTarget Manufacturer manufacturer);

}
