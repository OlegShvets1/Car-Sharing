package mate.academy.carsharing.service.user;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.exception.RoleNotFoundException;
import mate.academy.carsharing.mapper.UserMapper;
import mate.academy.carsharing.model.Role;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.role.RoleRepository;
import mate.academy.carsharing.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_ValidRequest_Success() {

        UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
                .email("bob@example.com")
                .password("12345678")
                .repeatPassword("12345678")
                .firstName("Bob")
                .lastName("Alison")
                .build();

        Role customerRole = new Role();
        customerRole.setName(Role.RoleName.CUSTOMER);
        when(roleRepository.findByName(Role.RoleName.CUSTOMER))
                .thenReturn(Optional.of(customerRole));

        User newUser = new User();
        newUser.setEmail(requestDto.getEmail());
        newUser.setPassword(requestDto.getPassword());
        newUser.addRole(customerRole);
        when(userMapper.mapToModel(requestDto)).thenReturn(newUser);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(requestDto.getEmail());
        savedUser.setFirstName(requestDto.getFirstName());
        savedUser.setLastName(requestDto.getLastName());
        when(userRepository.save(newUser)).thenReturn(savedUser);

        UserResponseDto expectedResponse = new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail()
        );

        when(userMapper.mapToDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.register(requestDto);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void register_AlreadyRegisteredUser_ThrowsException() {
        UserRegistrationRequestDto requestDto =
                createRegistrationRequestDto("bob@gmail.com", "12345678");

        User user = createUser(requestDto);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));

        String expected = "User with email - " + user.getEmail() + " already exists";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void updateUserRole_ValidRequest_Ok() {
        Long userId = 1L;
        User user = createUser();
        user.setId(userId);

        Role managerRole = new Role();
        managerRole.setName(Role.RoleName.MANAGER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.MANAGER)).thenReturn(Optional.of(managerRole));

        UserUpdatedRolesResponseDto expectedResponse = new UserUpdatedRolesResponseDto();
        expectedResponse.setRoleName(managerRole.getName().toString());

        user.addRole(managerRole);
        doReturn(user).when(userRepository).save(user);

        String roleName = "MANAGER";

        when(userMapper.toUpdatedRolesResponseDto(user)).thenReturn(expectedResponse);

        UserUpdatedRolesResponseDto actualResponse = userService.updateUserRole(userId, roleName);

        assertEquals(expectedResponse, actualResponse);
        assertTrue(user.getRoles().contains(managerRole));
    }

    @Test
    void updateUserRole_UserNotFound_ThrowsException() {
        Long userId = 1L;
        String roleName = "MANAGER";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUserRole(userId, roleName);
        });

        assertEquals("User with passed id - " + userId + " doesn't exist.", exception.getMessage());
    }

    @Test
    void updateUserRole_RoleNotFound_ThrowsException() {
        Long userId = 1L;
        User user = createUser();
        user.setId(userId);
        String roleName = "MANAGER";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.MANAGER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserRole(userId, roleName);
        });

        Throwable cause = exception.getCause();
        assertNotNull("Exception cause should not be null", cause);

        assertEquals(RoleNotFoundException.class, cause.getClass());

        assertEquals("One or more roles not found", cause.getMessage());
    }

    @Test
    void getProfileInfo_ValidUser_Ok() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setFirstName("Bob");
        user.setLastName("Alison");

        UserResponseDto expectedResponse = new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );

        when(userMapper.mapToDto(user)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.getProfileInfo(user);

        assertEquals(expectedResponse, actualResponse);
        verify(userMapper, times(1)).mapToDto(user);
    }

    @Test
    void updateProfileInfo_NoPasswordChange_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setFirstName("Bob");
        user.setLastName("Alison");

        User userCopy = new User();
        userCopy.setId(user.getId());
        userCopy.setEmail(user.getEmail());
        userCopy.setFirstName(user.getFirstName());
        userCopy.setLastName(user.getLastName());

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("bob@example.com");
        updatedUser.setFirstName("Boby");
        updatedUser.setLastName("Alison1");

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);
        updatedUser.setRoles(Set.of(role));

        UserUpdateProfileRequestDto requestDto = UserUpdateProfileRequestDto.builder()
                .firstName("Boby")
                .lastName("Alison1")
                .email("bob@example.com")
                .build();

        UserResponseDto expectedResponse = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail()
        );

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToDto(any(User.class))).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.updateProfileInfo(userCopy, requestDto);

        assertEquals(expectedResponse, actualResponse);

        verify(userMapper, times(1)).updateModel(userCopy, requestDto);
        verify(userRepository, times(1)).save(userCopy);
        verify(userMapper, times(1)).mapToDto(userCopy);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateProfileInfo_WithPasswordChange_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setFirstName("Bob");
        user.setLastName("Alison");
        user.setPassword("oldPassword");

        User userCopy = new User();
        userCopy.setId(user.getId());
        userCopy.setEmail(user.getEmail());
        userCopy.setFirstName(user.getFirstName());
        userCopy.setLastName(user.getLastName());
        userCopy.setPassword(user.getPassword());

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("bob@example.com");
        updatedUser.setFirstName("Bob");
        updatedUser.setLastName("Alison");
        updatedUser.setPassword("newPasswordHash");

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);
        updatedUser.setRoles(Set.of(role));

        UserResponseDto expectedResponse = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail()
        );

        UserUpdateProfileRequestDto requestDto = UserUpdateProfileRequestDto.builder()
                .firstName("Bob")
                .lastName("Alison")
                .email("bob@example.com")
                .password("newPassword")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToDto(any(User.class))).thenReturn(expectedResponse);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPasswordHash");

        UserResponseDto actualResponse = userService.updateProfileInfo(userCopy, requestDto);

        assertEquals(expectedResponse, actualResponse);

        verify(userMapper, times(1)).updateModel(userCopy, requestDto);
        verify(userRepository, times(1)).save(userCopy);
        verify(userMapper, times(1)).mapToDto(userCopy);
        verify(passwordEncoder, times(1))
                .encode("newPassword");
    }

    @Test
    void delete_ValidId_Success() {
        Long userId = 1L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void delete_NonExistentId_ThrowsException() {
        Long userId = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(userId);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            userService.delete(userId);
        });

        verify(userRepository, times(1)).deleteById(userId);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setPassword("12345678");
        user.setLastName("Bob");
        user.setFirstName("Alison");
        return user;
    }

    private User createUser(UserRegistrationRequestDto requestDto) {
        User user = new User();
        user.setId(1L);
        user.setPassword(requestDto.getPassword());
        user.setRoles(Set.of(new Role()));
        user.setEmail(requestDto.getEmail());
        user.setLastName(requestDto.getLastName());
        user.setFirstName(requestDto.getFirstName());
        return user;
    }

    private UserRegistrationRequestDto createRegistrationRequestDto(String email, String password) {
        return UserRegistrationRequestDto.builder()
                .firstName("Firstname")
                .lastName("LastName")
                .email(email)
                .password(password)
                .build();
    }
}
