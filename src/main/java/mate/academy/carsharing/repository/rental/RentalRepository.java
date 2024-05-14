package mate.academy.carsharing.repository.rental;

import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Car> {
    List<Rental> findAllByUserId(Long userId, Pageable pageable);

    List<Rental> findAllByUserId(Long userId);

    @Query("FROM Rental rental "
            + "WHERE (rental.isActive = true)"
            + "AND (rental.userId = :userId)")
    Optional<Rental> findActiveRentalByUserId(Long userId);

    @Query("FROM Rental rental "
            + "WHERE (rental.isActive = true)")
    List<Rental> findActiveRentals(Pageable pageable);
}
