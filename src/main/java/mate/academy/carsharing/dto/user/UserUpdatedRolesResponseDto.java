package mate.academy.carsharing.dto.user;

import java.util.Set;
import lombok.Data;
import mate.academy.carsharing.model.Role;

@Data
public class UserUpdatedRolesResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
}
