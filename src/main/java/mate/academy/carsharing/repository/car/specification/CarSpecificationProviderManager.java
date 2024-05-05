package mate.academy.carsharing.repository.car.specification;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.SpecificationProvider;
import mate.academy.carsharing.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationProviderManager implements SpecificationProviderManager<Car> {
    private final List<SpecificationProvider<Car>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Car> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Can't find correct specification provider by key: " + key));
    }
}
