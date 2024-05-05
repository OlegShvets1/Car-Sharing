package mate.academy.carsharing.service.rental;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import java.util.List;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.model.User;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto addRental(User user, Long carId, int daysToRent);

    List<RentalResponseDto> getUserRentals(Long userId, boolean isActive, Pageable pageable);

    RentalResponseDto getSpecificRental(Long id);

    RentalResponseDto setReturnDate(User rentalId) throws StripeException, MalformedURLException;

    List<RentalResponseDto> getAllRentals(User user, Pageable pageable);

    void cancel(User user);

    List<RentalResponseDto> getAllActive(Pageable pageable);
}
