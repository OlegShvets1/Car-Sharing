package mate.academy.carsharing.service.user;

import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.model.User;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserUpdatedRolesResponseDto updateUserRole(Long id, String roleName);

    UserResponseDto getProfileInfo(User user);

    UserResponseDto updateProfileInfo(User user, UserUpdateProfileRequestDto requestDto);

    void delete(Long id);
}
