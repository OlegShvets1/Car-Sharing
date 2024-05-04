package mate.academy.carsharing.dto.car;

import mate.academy.carsharing.model.Car;
import java.math.BigDecimal;

public record CarResponseDto(
        Long id,
        String model,
        String brand,
        Car.Type type,
        Integer inventory,
        BigDecimal dailyFee
) {

}
