package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.dto.user.UserUpdateProfileRequestDto;
import mate.academy.carsharing.dto.user.UserUpdatedRolesResponseDto;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users controller", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get user`s profile information",
            description = "Endpoint for getting user`s profile information")
    public UserResponseDto getMyProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update user`s profile information",
            description = "Endpoint for updating user`s profile information")
    public UserResponseDto updateProfile(
            Authentication authentication,
            @RequestBody UserUpdateProfileRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user, requestDto);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Update a user's role",
            description = "Endpoint for updating the user's role."
                    + " Allowed for managers only")
    public UserUpdatedRolesResponseDto updateUserRole(
            @PathVariable Long id,
            @RequestParam(name = "role_name") String roleName) {
        return userService.updateUserRole(id, roleName);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user",
            description = "Endpoint for deleting the user"
                    + " Allowed for managers only")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
