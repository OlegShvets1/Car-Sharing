package mate.academy.carsharing.dto.car;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CarSearchParametersDto(
        List<String> models,
        List<String> brands,
        List<String> types,
        @Size(min = 1, max = 2)
        List<BigDecimal> priceFork
) {
}
