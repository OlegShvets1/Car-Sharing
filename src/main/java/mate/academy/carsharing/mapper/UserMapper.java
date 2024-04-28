package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(imports = MapperConfig.class, componentModel = "spring")
public interface UserMapper {

    UserResponseDto mapToDto(User user);

    User mapToModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toResponseDto(User user);

    @Mapping(target = "password", ignore = true)
    void updateModel(@MappingTarget User user, UserUpdateProfileRequestDto requestDto);

    @Mapping(target = "rolesIds", ignore = true)
    UserUpdatedRolesResponseDto toUpdatedRolesResponseDto(User user);
}
