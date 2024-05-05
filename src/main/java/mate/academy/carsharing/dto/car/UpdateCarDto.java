package mate.academy.carsharing.dto.car;

import java.math.BigDecimal;

public record UpdateCarDto(
        String model,
        String brand,
        Integer inventory,
        BigDecimal dailyFee,
        String type
) {
}
