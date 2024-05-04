package mate.academy.carsharing.repository;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface SpecificationProvider<T> {
    String getKey();

    Specification<T> getSpecification(List<String> params);
}
