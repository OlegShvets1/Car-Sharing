package mate.academy.carsharing.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdatedRolesResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
}
