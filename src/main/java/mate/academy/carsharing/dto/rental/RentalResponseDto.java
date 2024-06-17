package mate.academy.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate requiredReturnDate;
    private String actualReturnDate;
    private Long carId;
    private Long userId;
    private boolean isActive;
}
