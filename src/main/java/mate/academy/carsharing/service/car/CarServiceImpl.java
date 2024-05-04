package mate.academy.carsharing.service.car;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.dto.car.CarSearchParametersDto;
import mate.academy.carsharing.dto.car.CreateCarRequestDto;
import mate.academy.carsharing.dto.car.UpdateCarDto;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.car.specification.CarSpecificationBuilder;
import mate.academy.carsharing.repository.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
        private final CarMapper carMapper;
    private final CarSpecificationBuilder carSpecificationBuilder;

    @Override
    public CarResponseDto create(CreateCarRequestDto requestDto) {
        Car car = carMapper.mapToModel(requestDto);
        carRepository.save(car);
        return carMapper.mapToResponseDto(car);
    }

    @Override
    public CarResponseDto update(Long id, UpdateCarDto updateCarDto) {
        carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id - " + id));
        Car updatedCar = carMapper.mapToModel((updateCarDto));
        updatedCar.setId(id);
        carRepository.save(updatedCar);
        return carMapper.mapToResponseDto(updatedCar);
    }

    @Override
    public void delete(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't find a car by id " + id));
        carRepository.deleteById(id);
    }

    @Override
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable)
                .stream()
                .map(carMapper::mapToResponseDto)
                .toList();
    }

    @Override
    public CarResponseDto getInfo(Long id) {
        return carRepository.findById(id)
                .map(carMapper::mapToResponseDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Сar with id - " + id + " not available" ));
    }


    @Override
    public List<CarResponseDto> search(CarSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Car> carSpecification = carSpecificationBuilder.build(searchParameters);
        return carRepository.findAll(carSpecification, pageable)
                .stream()
                .map(carMapper::mapToResponseDto)
                .toList();
    }
}
