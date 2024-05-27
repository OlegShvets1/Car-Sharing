package mate.academy.carsharing.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Component;

@Component
public interface StripeService {
    public Session createSession(Long price, String unitName) throws StripeException;
}
