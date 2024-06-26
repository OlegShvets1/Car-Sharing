package mate.academy.carsharing.dto.car;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateCarDto(
        String model,
        String brand,
        Integer inventory,
        BigDecimal dailyFee,
        @NotNull
        String type
) {
}
