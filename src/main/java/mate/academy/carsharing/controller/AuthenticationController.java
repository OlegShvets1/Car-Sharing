package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.user.UserLoginRequestDto;
import mate.academy.carsharing.dto.user.UserLoginResponseDto;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.security.AuthenticationService;
import mate.academy.carsharing.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User authentication", description = "Authentication a new user")
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@Validated
@RestController
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration",
            description = "register a new user")
    @PostMapping(value = "/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Authentication", description = "User authentication")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
