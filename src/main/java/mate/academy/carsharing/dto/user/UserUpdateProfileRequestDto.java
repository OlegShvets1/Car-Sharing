package mate.academy.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateProfileRequestDto {
    private String firstName;
    private String lastName;
    private String password;
    @Email
    private String email;
}
