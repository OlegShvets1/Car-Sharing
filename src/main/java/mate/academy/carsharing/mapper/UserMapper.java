package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.model.User;
import org.mapstruct.Mapper;

@Mapper(imports = MapperConfig.class, componentModel = "spring")
public interface UserMapper {

    UserResponseDto mapToDto(User user);

    User mapToModel(UserRegistrationRequestDto requestDto);
}
