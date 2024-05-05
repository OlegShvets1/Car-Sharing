package mate.academy.carsharing.dto.car;

import java.math.BigDecimal;
import mate.academy.carsharing.model.Car;

public record CarResponseDto(
        Long id,
        String model,
        String brand,
        Car.Type type,
        Integer inventory,
        BigDecimal dailyFee
) {

}
