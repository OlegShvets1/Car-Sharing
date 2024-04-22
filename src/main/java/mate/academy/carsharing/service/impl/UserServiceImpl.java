package mate.academy.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.mapper.UserMapper;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.UserRepository;
import mate.academy.carsharing.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: "
                    + requestDto.getEmail() + " already exist");
        }
        User newUser = userMapper.mapToModel(requestDto);
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User savedUser = userRepository.save(newUser);

        return userMapper.mapToDto(savedUser);
    }
}
