package mate.academy.carsharing.service.payment.strategy;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Payment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategy {
    private final List<PaymentService> paymentServices;

    public PaymentService getPaymentService(Payment.Type type) {
        return paymentServices
                .stream()
                .filter(paymentService ->
                        paymentService.isApplicable(type))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find payment service by payment type " + type));
    }
}
