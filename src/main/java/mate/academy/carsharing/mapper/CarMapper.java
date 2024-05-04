package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.dto.car.CreateCarRequestDto;
import mate.academy.carsharing.dto.car.UpdateCarDto;
import mate.academy.carsharing.model.Car;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(imports = MapperConfig.class, componentModel = "spring")
public interface CarMapper {
    CarResponseDto mapToResponseDto(Car car);

    @Mapping(target = "type", ignore = true)
    Car mapToModel(CreateCarRequestDto requestDto);

    @Mapping(target = "type", ignore = true)
    Car mapToModel(UpdateCarDto updateCarDto);

    @Mapping(target = "type", ignore = true)
    void mapToModel(@MappingTarget Car car, UpdateCarDto updateDto);

    @AfterMapping
    default void setType(@MappingTarget Car car, UpdateCarDto updateDto) {
        if (updateDto.type() != null) {
            Car.Type type = Car.Type.fromString(updateDto.type());
            car.setType(type);
        }
    }

    @AfterMapping
    default void setType(@MappingTarget Car.CarBuilder carBuilder, CreateCarRequestDto requestDto) {
        Car.Type type = Car.Type.fromString(requestDto.type());
        carBuilder.type(type);
    }
}




