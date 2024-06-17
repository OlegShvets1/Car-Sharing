package mate.academy.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RentalRequestDto {
    @NotNull
    private LocalDate rentalDate;
    @NotNull
    private LocalDate requiredReturnDate;
    @NotNull
    private Long carId;
}
