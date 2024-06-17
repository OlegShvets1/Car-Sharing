package mate.academy.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.car.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {
    @Autowired
    private CarRepository carRepository;

    @Test
    @Sql(scripts = "classpath:database/cars/remove-cars-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/add-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-cars-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAvailableCars_Ok() {

        Car teslaModelS = createCar(
                4L, "Tesla",
                "Model S",
                Car.Type.UNIVERSAL,
                BigDecimal.valueOf(229)
        );

        Car toyotaRav = createCar(
                5L, "Toyota",
                "RAV-4",
                Car.Type.SUV,
                BigDecimal.valueOf(199));

        List<Car> expectedList = List.of(teslaModelS, toyotaRav);

        List<Car> actualList =
                carRepository.findAllAvailable(PageRequest.of(0, 10));

        assertEquals(expectedList, actualList);
        assertEquals(expectedList.get(0), actualList.get(0));
        assertEquals(expectedList.get(1), actualList.get(1));

        expectedList = List.of();
        actualList = carRepository.findAllAvailable(PageRequest.of(1, 10));

        assertEquals(expectedList, actualList);
        assertTrue(actualList.isEmpty());
    }

    private Car createCar(Long id, String brand, String model, Car.Type type, BigDecimal dailyFee) {
        Car car = Car.builder()
                .brand(brand)
                .model(model)
                .type(type)
                .inventory(5)
                .dailyFee(dailyFee)
                .isDeleted(false)
                .build();
        car.setId(id);
        return car;
    }
}
