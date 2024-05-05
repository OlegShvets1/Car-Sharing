package mate.academy.carsharing.repository.car.specification;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.carsharing.model.Car;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DailyFeeSpecificationProvider {
    private static final String PRICE = "dailyFee";

    public static Specification<Car> getSpecification(List<BigDecimal> params) {
        BigDecimal priceMin;
        BigDecimal priceMax;
        if (params.size() == 1) {
            priceMin = BigDecimal.ZERO;
            priceMax = params.get(0);
        } else {
            priceMin = params.get(0);
            priceMax = params.get(1);
        }
        if (priceMin.compareTo(priceMax) >= 0) {
            throw new IllegalArgumentException(
                    "Price to should be greater than price price from, but was " + params);
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.between(
                root.get(PRICE),
                priceMin,
                priceMax);
    }
}
