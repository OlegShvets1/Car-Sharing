package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.model.Role;
import mate.academy.carsharing.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(imports = MapperConfig.class, componentModel = "spring")
public interface UserMapper {

    UserResponseDto mapToDto(User user);

    User mapToModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "password", ignore = true)
    void updateModel(@MappingTarget User user, UserUpdateProfileRequestDto requestDto);

    UserUpdatedRolesResponseDto toUpdatedRolesResponseDto(User user);

    @AfterMapping
    default void setRolesName(
            @MappingTarget UserUpdatedRolesResponseDto responseDto,
            User user) {
        Set<String> roleName = new HashSet<>();
        roleName = user.getRoles()
                .stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toSet());
        responseDto.setRoleName(String.valueOf(roleName));
    }
}
