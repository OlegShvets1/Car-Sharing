package mate.academy.carsharing.repository.car.specification;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.car.CarSearchParametersDto;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.SpecificationBuilder;
import mate.academy.carsharing.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationBuilder implements SpecificationBuilder<Car> {
    private static final String MODEL = "model";
    private static final String BRAND = "brand";
    private final SpecificationProviderManager<Car> specificationProviderManager;
    private final TypeSpecificationProvider typeSpecificationProvider;
    private final DailyFeeSpecificationProvider dailyFeeSpecificationProvider;

    @Override
    public Specification<Car> build(CarSearchParametersDto searchParameters) {
        Specification<Car> spec = Specification.where(null);

        if (searchParameters != null) {
            if (searchParameters.models() != null && !searchParameters.models().isEmpty()) {
                spec = spec.and(specificationProviderManager
                        .getSpecificationProvider(MODEL)
                        .getSpecification((searchParameters.models())));
            }
            if (searchParameters.brands() != null && !searchParameters.brands().isEmpty()) {
                spec = spec.and(specificationProviderManager
                        .getSpecificationProvider(BRAND)
                        .getSpecification((searchParameters.brands())));
            }
            if (searchParameters.types() != null && !searchParameters.types().isEmpty()) {
                spec = spec.and(typeSpecificationProvider
                        .getSpecification(searchParameters.types()));
            }
            if (!searchParameters.priceFork().isEmpty()) {
                spec = spec.and(DailyFeeSpecificationProvider
                        .getSpecification(searchParameters.priceFork()));
            }
        }
        return spec;
    }
}
