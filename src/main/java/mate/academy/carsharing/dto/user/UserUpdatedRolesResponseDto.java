package mate.academy.carsharing.dto.user;

import lombok.Data;

@Data
public class UserUpdatedRolesResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
}
