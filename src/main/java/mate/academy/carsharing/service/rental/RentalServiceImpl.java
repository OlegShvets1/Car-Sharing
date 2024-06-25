package mate.academy.carsharing.service.rental;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.exception.CarRentalException;
import mate.academy.carsharing.exception.InvalidRequestException;
import mate.academy.carsharing.mapper.RentalMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.rental.RentalRepository;
import mate.academy.carsharing.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int DECREASE_BY_ONE = 1;
    private static final int INCREASE_BY_ONE = 1;
    private static final int ZERO_INVENTORY = 0;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public RentalResponseDto addRental(User user, RentalRequestDto requestDto) {
        checkIfRentalAlreadyExists(user);
        if (requestDto.getRequiredReturnDate() == null || user.getId() == null) {
            throw new InvalidRequestException("Required fields are missing in the request.");
        }
        ifAvailable(requestDto.getCarId());
        Rental newRental = rentalMapper.mapToModel(requestDto);
        newRental.setUserId(user.getId());
        newRental.setActive(true);
        Long carIdForUpdate = newRental.getCarId();
        Car carFromDb = carRepository.findById(carIdForUpdate).orElseThrow(() ->
                new EntityNotFoundException("Car with id - " + carIdForUpdate
                        + " does not exist in DB"));
        int currentNumbersOfCar = carFromDb.getInventory();
        int numbersCarAfterUpdate = currentNumbersOfCar - DECREASE_BY_ONE;
        carFromDb.setInventory(numbersCarAfterUpdate);
        Car savedCar = carRepository.save(carFromDb);
        Rental savedRental = rentalRepository.save(newRental);
        notificationService.rentalCreationNotification(rentalMapper.toResponseDto(savedRental));
        return rentalMapper.toResponseDto(savedRental);
    }

    @Override
    public List<RentalResponseDto> getUserRentals(Long userId,
                                                  boolean isActive,
                                                  Pageable pageable) {
        if (isActive) {
            Rental activeRental = rentalRepository.findActiveRentalByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User doesn't have an active rental"));
            return mapToResponseDto(List.of(activeRental));
        } else {
            List<Rental> userRentals = rentalRepository.findAllByUserId(userId);
            List<Rental> nonActiveRentals = userRentals.stream()
                    .filter(rental -> rental.getActualReturnDate() != null && !rental.isDeleted())
                    .collect(Collectors.toList());
            return mapToResponseDto(nonActiveRentals);
        }
    }

    @Override
    public RentalResponseDto setReturnDate(User user) {
        Rental rental = rentalRepository.findActiveRentalByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "In this moment you don't have an active rental"));
        LocalDate now = LocalDate.now();
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car by id " + rental.getCarId()));
        rental.setActualReturnDate(now);
        rental.setActive(false);
        car.setInventory(car.getInventory() + INCREASE_BY_ONE);
        carRepository.save(car);
        rentalRepository.save(rental);
        return rentalMapper.toResponseDto(rental);
    }

    @Override
    public RentalResponseDto getSpecificRental(Long id) {
        return rentalRepository.findById(id)
                .map(rentalMapper::toResponseDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("There is no rental by id " + id));
    }

    @Override
    public List<RentalResponseDto> getAllRentals(User user, Pageable pageable) {
        return mapToResponseDto(
                rentalRepository.findAllByUserId(
                        user.getId(),
                        pageable));
    }

    @Override
    public List<RentalResponseDto> getAllActiveRentals(Pageable pageable) {
        return rentalRepository.findActiveRentals(pageable)
                .stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void ifAvailable(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException("There is no car with id - " + carId));
        if (car.getInventory() <= ZERO_INVENTORY) {
            throw new CarRentalException("Sorry, this car is not available now."
                    + " You can choose any other car.");
        }
    }

    private List<RentalResponseDto> mapToResponseDto(List<Rental> rentals) {
        return rentals
                .stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void checkIfRentalAlreadyExists(User user) {
        if (rentalRepository.findActiveRentalByUserId(user.getId())
                .isPresent()) {
            throw new CarRentalException(
                    "You already have a pending or ongoing lease, pay or cancel it first."
                            + " After that you can choose another car");
        }
    }
}
