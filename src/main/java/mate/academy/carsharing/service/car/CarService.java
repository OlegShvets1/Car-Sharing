package mate.academy.carsharing.service.car;

import java.util.List;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.dto.car.CarSearchParametersDto;
import mate.academy.carsharing.dto.car.CreateCarRequestDto;
import mate.academy.carsharing.dto.car.UpdateCarDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto create(CreateCarRequestDto requestDto);

    CarResponseDto update(Long id, UpdateCarDto updateDto);

    void delete(Long id);

    List<CarResponseDto> getAllCars(Pageable pageable);

    CarResponseDto getInfo(Long id);

    List<CarResponseDto> search(CarSearchParametersDto parametersDto, Pageable pageable);
}
