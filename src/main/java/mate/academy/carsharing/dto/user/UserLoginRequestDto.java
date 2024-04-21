package mate.academy.carsharing.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class UserLoginRequestDto {

    @NotBlank
    @Length(min = 8, max = 50)
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
