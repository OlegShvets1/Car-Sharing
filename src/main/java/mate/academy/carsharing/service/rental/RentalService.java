package mate.academy.carsharing.service.rental;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import java.util.List;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.model.User;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto addRental(User user, RentalRequestDto requestDto);

    List<RentalResponseDto> getUserRentals(Long userId, boolean isActive, Pageable pageable);

    RentalResponseDto setReturnDate(User rentalId) throws StripeException, MalformedURLException;

    RentalResponseDto getSpecificRental(Long id);

    List<RentalResponseDto> getAllRentals(User user, Pageable pageable);

    List<RentalResponseDto> getAllActiveRentals(Pageable pageable);
}
