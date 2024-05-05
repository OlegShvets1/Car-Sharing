package mate.academy.carsharing.repository.rental;

import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Car> {

}
