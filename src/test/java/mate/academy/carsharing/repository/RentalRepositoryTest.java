package mate.academy.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.repository.rental.RentalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @Sql(scripts = "classpath:database/cars/add-rentals-to-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-rentals-from-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllRentalsByUserId_ValidParam_Ok() {
        Rental firstRental = createRental("2024-06-01",
                "2024-06-10", null, 1L, 2L, true);
        firstRental.setId(2L);

        Rental secondRental = createRental("2024-06-01",
                "2024-06-10", "2024-06-10", 2L, 2L, true);
        secondRental.setId(3L);

        List<Rental> expected = List.of(firstRental, secondRental);

        List<Rental> actual = rentalRepository.findAllByUserId(2L);

        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/cars/add-rentals-to-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-rentals-from-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findActiveRentalByUserId_Ok() {
        Rental expected = createRental("2024-06-01", "2024-06-10",
                null, 1L, 1L, true);
        expected.setId(1L);

        Optional<Rental> actual = rentalRepository.findActiveRentalByUserId(1L);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @Sql(scripts = "classpath:database/cars/add-rentals-to-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-rentals-from-rentals-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllActiveRentals_ValidParams_Ok() {
        Rental firstRental = createRental("2024-06-01", "2024-06-10", null, 1L, 1L, true);
        firstRental.setId(1L);

        Rental secondRental = createRental("2024-06-01", "2024-06-10", null, 1L, 2L, true);
        secondRental.setId(2L);

        Rental thirthRental = createRental("2024-06-01", "2024-06-10", "2024-06-10", 2L, 2L, true);
        thirthRental.setId(3L);

        List<Rental> expected = List.of(firstRental, secondRental, thirthRental);

        List<Rental> actual = rentalRepository.findActiveRentals(PageRequest.of(0, 5));

        assertEquals(expected, actual);
    }

    private Rental createRental(String rentalDate, String requiredReturnDate,
                String actualReturnDate, Long carId, Long userId, boolean isActive) {
        return Rental.builder()
                .rentalDate(LocalDate.parse(rentalDate))
                .requiredReturnDate(LocalDate.parse(requiredReturnDate))
                .actualReturnDate(actualReturnDate != null ? LocalDate
                .parse(actualReturnDate) : null)
                .carId(carId)
                .userId(userId)
                .isActive(isActive)
                .build();
    }
}
