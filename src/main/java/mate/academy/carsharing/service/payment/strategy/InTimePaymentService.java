package mate.academy.carsharing.service.payment.strategy;

import java.math.BigDecimal;
import mate.academy.carsharing.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class InTimePaymentService implements PaymentService {
    private static final long DAYS_TO_ADD = 1L;

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long days) {

        return dailyFee.multiply(
                BigDecimal.valueOf(days + DAYS_TO_ADD)
        ).multiply(BigDecimal.valueOf(100));
    }

    @Override
    public boolean isApplicable(Payment.Type type) {
        return type.equals(Payment.Type.PAYMENT);
    }
}
