package mate.academy.carsharing.service.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.mapper.UserMapper;
import mate.academy.carsharing.model.Role;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.role.RoleRepository;
import mate.academy.carsharing.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email- "
                    + requestDto.getEmail() + " already exist");
        }
        Optional<Role> customerRoleOptional = roleRepository.findByName(Role.RoleName.CUSTOMER);
        if (customerRoleOptional.isPresent()) {
            Role customerRole = customerRoleOptional.get();
            User newUser = userMapper.mapToModel(requestDto);
            newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            newUser.addRole(customerRole);
            User savedUser = userRepository.save(newUser);
            return userMapper.mapToDto(savedUser);
        } else {
            try {
                throw new RoleNotFoundException("Role 'CUSTOMER' not found");
            }
            catch (RoleNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Transactional
    @Override
    public UserUpdatedRolesResponseDto updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "User with passed id - " + id + " doesn't exist." ));
        Optional<Role> newRole = roleRepository.findByName(Role.RoleName.MANAGER);
          if (newRole.isPresent()){
            user.addRole(newRole.get());
            user = userRepository.save(user);
        } else {
            try {
                throw new RoleNotFoundException("One or more roles not found");
            } catch (RoleNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return userMapper.toUpdatedRolesResponseDto(user);
    }

    @Override
    public UserResponseDto getProfileInfo(User user) {
        return userMapper.mapToDto(user);
    }

    @Override
    public UserResponseDto updateProfileInfo(User user, UserUpdateProfileRequestDto requestDto) {
        userMapper.updateModel(user, requestDto);
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        userRepository.save(user);
        return userMapper.mapToDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
