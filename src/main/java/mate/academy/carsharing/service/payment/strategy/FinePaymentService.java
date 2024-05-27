package mate.academy.carsharing.service.payment.strategy;

import java.math.BigDecimal;
import mate.academy.carsharing.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class FinePaymentService implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(2);

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long days) {

        return dailyFee.multiply(
                        BigDecimal.valueOf(days)
                ).multiply(FINE_MULTIPLIER)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public boolean isApplicable(Payment.Type type) {
        return type.equals(Payment.Type.FINE);
    }
}
