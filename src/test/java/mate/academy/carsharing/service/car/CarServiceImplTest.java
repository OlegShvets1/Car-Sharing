package mate.academy.carsharing.service.car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.dto.car.CarSearchParametersDto;
import mate.academy.carsharing.dto.car.CreateCarRequestDto;
import mate.academy.carsharing.dto.car.UpdateCarDto;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.car.specification.CarSpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @Mock
    private CarSpecificationBuilder carSpecificationBuilder;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void create_ValidCar_Ok() {
        CreateCarRequestDto requestDto = new CreateCarRequestDto(
                "Model 3",
                "Tesla",
                "SUV",
                5,
                BigDecimal.valueOf(229)
        );

        Car car = Car.builder()
                .model(requestDto.model())
                .type(Car.Type.SUV)
                .inventory(requestDto.inventory())
                .dailyFee(requestDto.dailyFee())
                .brand(requestDto.brand())
                .id(1L)
                .build();

        CarResponseDto expected = new CarResponseDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType(),
                car.getInventory(),
                car.getDailyFee()
        );

        when(carMapper.mapToModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.mapToResponseDto(car)).thenReturn(expected);

        CarResponseDto actual = carService.create(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    void getAllCars_Ok() {
        Car firstCar = createCar(1L, "Tesla", "Model - 3");
        Car secondCar = createCar(2L, "BMW", "X - 5");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Car> carPage = new PageImpl<>(List.of(firstCar, secondCar));

        CarResponseDto firstDto = createResponseDto(firstCar);
        CarResponseDto secondDto = createResponseDto(secondCar);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.mapToResponseDto(firstCar)).thenReturn(firstDto);
        when(carMapper.mapToResponseDto(secondCar)).thenReturn(secondDto);

        List<CarResponseDto> expected = List.of(firstDto, secondDto);
        List<CarResponseDto> actual = carService.getAllCars(pageable);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected, actual);

        verify(carRepository, times(1)).findAll(pageable);
        verify(carMapper, times(2)).mapToResponseDto(any());
        verifyNoMoreInteractions(carRepository);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    void getInfo_ValidParam_Ok() {
        Car car = createCar(1L, "Tesla", "Model - 3");

        Optional<Car> carOptional = Optional.of(car);

        CarResponseDto expected = createResponseDto(car);

        when(carRepository.findById(car.getId())).thenReturn(carOptional);
        when(carMapper.mapToResponseDto(car)).thenReturn(expected);

        CarResponseDto actual = carService.getInfo(car.getId());

        assertEquals(expected, actual);

        verify(carRepository, times(1)).findById(any());
        verify(carMapper, times(1)).mapToResponseDto(any());
        verifyNoMoreInteractions(carRepository);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    void findById_NotValidId_ThrowsException() {
        when(carRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> carService.getInfo(1L));

        String expected = "Сar with id - 1 not available";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void getInfo_NotValidId_ThrowsException() {
        Long notValidId = 100L;

        when(carRepository.findById(notValidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> carService.getInfo(notValidId));

        String expected = "Сar with id - " + notValidId + " not available";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void search_EmptyRequest_ThrowsException() {
        CarSearchParametersDto emptyParams = new CarSearchParametersDto(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        when(carSpecificationBuilder.build(emptyParams)).thenThrow(new IllegalArgumentException(
                "Searching should be done by at least one param, but was 0"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.search(emptyParams, pageable));

        String expected = "Searching should be done by at least one param, but was 0";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    private CarSearchParametersDto createSearchParams(String model) {
        return new CarSearchParametersDto(
                List.of(model), List.of(),
                null,
                null);
    }

    private UpdateCarDto createUpdateDto(Car car, String brand, String model) {
        return new UpdateCarDto(
                model,
                brand,
                car.getInventory(),
                car.getDailyFee(),
                car.getType().name());
    }

    private CarResponseDto createResponseDto(Car car) {
        return new CarResponseDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType(),
                car.getInventory(),
                car.getDailyFee());
    }

    private Car createCar(Long id, String brand, String model) {
        Car car = Car.builder()
                .brand(brand)
                .dailyFee(BigDecimal.TEN)
                .inventory(10)
                .type(Car.Type.UNIVERSAL)
                .model(model)
                .id(id)
                .build();
        return car;
    }
}
