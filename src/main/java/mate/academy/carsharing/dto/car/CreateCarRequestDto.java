package mate.academy.carsharing.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotBlank
        String model,
        @NotBlank
        String brand,
        @NotBlank
        String type,
        @Positive
        Integer inventory,
        @Positive
        BigDecimal dailyFee
) {
}
