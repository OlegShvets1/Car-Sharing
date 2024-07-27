package mate.academy.carsharing.repository.car;

import java.util.List;
import mate.academy.carsharing.model.Car;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends JpaRepository<Car, Long>,
        JpaSpecificationExecutor<Car> {
    @Query("FROM Car car WHERE car.inventory > 0")
    List<Car> findAllAvailable(Pageable pageable);
}
