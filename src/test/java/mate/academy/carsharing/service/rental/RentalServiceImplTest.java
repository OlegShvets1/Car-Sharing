package mate.academy.carsharing.service.rental;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.exception.CarRentalException;
import mate.academy.carsharing.mapper.RentalMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.rental.RentalRepository;
import mate.academy.carsharing.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    void addRental_ValidParams_Ok() {
        User user = new User();
        user.setId(1L);

        Car car = createCar("Tesla", "Model 3");
        car.setId(1L);

        RentalRequestDto requestDto = RentalRequestDto.builder()
                .rentalDate(LocalDate.now())
                .requiredReturnDate(LocalDate.now().plusDays(3))
                .carId(car.getId())
                .build();

        Rental rental = createRental(true, car.getId(), user.getId(),
                LocalDate.now(), LocalDate.now().plusDays(5));
        RentalResponseDto expected = createResponseDto(rental);

        when(rentalRepository.findActiveRentalByUserId(user.getId())).thenReturn(Optional.empty());
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalMapper.mapToModel(requestDto)).thenReturn(rental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);

        RentalResponseDto actual = rentalService.addRental(user, requestDto);

        assertEquals(expected, actual);

        verify(notificationService).rentalCreationNotification(expected);
    }

    @Test
    void addRental_RentalIsAlreadyCreated_ThrowsException() {
        User user = new User();
        user.setId(1L);

        RentalRequestDto requestDto = RentalRequestDto.builder()
                .rentalDate(LocalDate.now())
                .requiredReturnDate(LocalDate.now().plusDays(5))
                .carId(1L)
                .build();

        when(rentalRepository.findActiveRentalByUserId(user.getId()))
                .thenReturn(Optional.of(new Rental()));

        CarRentalException exception = assertThrows(CarRentalException.class, () ->
                rentalService.addRental(user, requestDto));

        String expected = "You already have a pending or ongoing lease,"
                + " pay or cancel it first. After that you can choose another car";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void addRental_InvalidCarId_ThrowsException() {
        User user = new User();
        user.setId(1L);

        RentalRequestDto requestDto = RentalRequestDto.builder()
                .rentalDate(LocalDate.now())
                .requiredReturnDate(LocalDate.now().plusDays(3))
                .carId(1L)
                .build();

        when(rentalRepository.findActiveRentalByUserId(user.getId())).thenReturn(Optional.empty());
        when(carRepository.findById(requestDto.getCarId())).thenReturn(Optional.empty());

        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> rentalService.addRental(user, requestDto));

        String expected = "There is no car with id - " + requestDto.getCarId();
        String actual = notFoundException.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void addRental_InvalidCarInventory_ThrowsException() {
        User user = new User();
        user.setId(1L);

        Car car = createCar("Tesla", "Model 3");
        car.setId(1L);
        car.setInventory(0);

        RentalRequestDto requestDto = RentalRequestDto.builder()
                .rentalDate(LocalDate.now())
                .requiredReturnDate(LocalDate.now().plusDays(3))
                .carId(car.getId())
                .build();

        when(rentalRepository.findActiveRentalByUserId(user.getId())).thenReturn(Optional.empty());
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        CarRentalException noInventoryException = assertThrows(CarRentalException.class,
                () -> rentalService.addRental(user, requestDto));

        String expected = "Sorry, this car is not available now. You can choose any other car.";
        String actual = noInventoryException.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void getUserRentals_ActiveRentals_ReturnsValidResponse() {
        Rental rental = createRental(true, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(5));
        RentalResponseDto expected = createResponseDto(rental);

        when(rentalRepository.findActiveRentalByUserId(1L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);

        List<RentalResponseDto> actual = rentalService.getUserRentals(1L,
                true, PageRequest.of(0, 5));

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserRentals_NotActiveRentals_Ok() {
        Rental rental = createRental(false, 1L, 1L, LocalDate.now(),
                LocalDate.now().plusDays(5));
        rental.setActualReturnDate(LocalDate.now());
        RentalResponseDto dto = createResponseDto(rental);

        List<Rental> rentals = List.of(rental);

        when(rentalRepository.findAllByUserId(1L)).thenReturn(rentals);
        when(rentalMapper.toResponseDto(rental)).thenReturn(dto);

        List<RentalResponseDto> actual = rentalService.getUserRentals(1L,
                false, PageRequest.of(0, 5));

        assertEquals(List.of(dto), actual);
    }

    @Test
    void setReturnDate_Success() {
        User user = new User();
        user.setId(1L);

        Car car = new Car();
        car.setId(1L);
        car.setInventory(5);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCarId(car.getId());
        rental.setUserId(user.getId());
        rental.setActive(true);

        LocalDate now = LocalDate.now();

        RentalResponseDto expectedResponseDto = new RentalResponseDto();
        expectedResponseDto.setId(rental.getId());
        expectedResponseDto.setCarId(car.getId());
        expectedResponseDto.setUserId(user.getId());
        expectedResponseDto.setActualReturnDate(now.toString());
        expectedResponseDto.setActive(false);

        when(rentalRepository.findActiveRentalByUserId(user.getId())).thenReturn(Optional
                .of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalMapper.toResponseDto(any(Rental.class))).thenReturn(expectedResponseDto);

        RentalResponseDto actualResponseDto = rentalService.setReturnDate(user);

        assertNotNull(actualResponseDto);
        assertEquals(expectedResponseDto.getId(), actualResponseDto.getId());
        assertEquals(expectedResponseDto.getCarId(), actualResponseDto.getCarId());
        assertEquals(expectedResponseDto.getUserId(), actualResponseDto.getUserId());
        assertEquals(expectedResponseDto.getActualReturnDate(), actualResponseDto
                .getActualReturnDate());
        assertEquals(expectedResponseDto.isActive(), actualResponseDto.isActive());

        verify(rentalRepository, times(1)).findActiveRentalByUserId(user.getId());
        verify(carRepository, times(1)).findById(car.getId());
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(rentalMapper, times(1)).toResponseDto(any(Rental.class));

        assertEquals(6, car.getInventory());
        assertFalse(rental.isActive());
        assertEquals(now.toString(), rental.getActualReturnDate().toString());
    }

    @Test
    void setReturnDate_NoActiveRental_ThrowsException() {
        User user = new User();
        user.setId(1L);

        when(rentalRepository.findActiveRentalByUserId(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            rentalService.setReturnDate(user);
        });

        assertEquals("In this moment you don't have an active rental", thrown.getMessage());

        verify(rentalRepository, times(1))
                .findActiveRentalByUserId(user.getId());
        verifyNoInteractions(carRepository);
        verifyNoInteractions(rentalMapper);
    }

    @Test
    void setReturnDate_CarNotFound_ThrowsException() {
        User user = new User();
        user.setId(1L);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCarId(1L);
        rental.setUserId(user.getId());
        rental.setActive(true);

        when(rentalRepository.findActiveRentalByUserId(user.getId()))
                .thenReturn(Optional.of(rental));
        when(carRepository.findById(rental.getCarId())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            rentalService.setReturnDate(user);
        });

        assertEquals("Can't find car by id " + rental.getCarId(), thrown.getMessage());

        verify(rentalRepository, times(1))
                .findActiveRentalByUserId(user.getId());
        verify(carRepository, times(1)).findById(rental.getCarId());
        verifyNoInteractions(rentalMapper);
    }

    @Test
    void getAllRentals_FindAllByUserId() {
        User user = new User();
        user.setId(1L);

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        rentalService.getAllRentals(user, pageable);

        verify(rentalRepository, times(1)).findAllByUserId(user.getId(), pageable);
    }

    @Test
    void getSpecificRental_ValidParam_Ok() {
        Rental rental = createRental(true, 1L, 1L, LocalDate.now(),
                LocalDate.now().plusDays(5));
        rental.setId(1L);

        RentalResponseDto expected = createResponseDto(rental);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDto(rental)).thenReturn(expected);

        RentalResponseDto actual = rentalService.getSpecificRental(1L);

        assertEquals(expected, actual);
    }

    private Car createCar(String brand, String model) {
        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setInventory(5);
        return car;
    }

    private Rental createRental(boolean isActive, Long carId, Long userId,
                                LocalDate rentalDate, LocalDate requiredReturnDate) {
        return Rental.builder()
                .rentalDate(rentalDate)
                .requiredReturnDate(requiredReturnDate)
                .carId(carId)
                .userId(userId)
                .isActive(isActive)
                .build();
    }

    private RentalResponseDto createResponseDto(Rental rental) {
        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setRentalDate(rental.getRentalDate());
        responseDto.setRequiredReturnDate(rental.getRequiredReturnDate());
        responseDto.setActualReturnDate(rental.getActualReturnDate()
                != null ? rental.getActualReturnDate().toString() : null);
        responseDto.setCarId(rental.getCarId());
        responseDto.setUserId(rental.getUserId());
        responseDto.setActive(rental.isActive());
        return responseDto;
    }
}
