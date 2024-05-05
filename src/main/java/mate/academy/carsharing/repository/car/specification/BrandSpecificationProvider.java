package mate.academy.carsharing.repository.car.specification;

import java.util.List;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BrandSpecificationProvider implements SpecificationProvider<Car> {
    private static final String BRAND = "brand";

    @Override
    public String getKey() {
        return BRAND;
    }

    public Specification<Car> getSpecification(List<String> params) {
        return (root, query, criteriaBuilder)
                -> root.get(BRAND).in(params);
    }
}
