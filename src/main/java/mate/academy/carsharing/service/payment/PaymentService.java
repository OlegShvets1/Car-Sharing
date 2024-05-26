package mate.academy.carsharing.service.payment;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import java.util.List;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.model.User;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    List<PaymentResponseDto> getUserPayments(Long userId, Pageable pageable);

    PaymentResponseDto create(User user)
            throws StripeException, MalformedURLException;

    PaymentResponseDto success(User user);

    void cancel(User user);

    PaymentResponseDto getMyPayment(User user);
}
