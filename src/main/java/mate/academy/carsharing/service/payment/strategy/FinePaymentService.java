package mate.academy.carsharing.service.payment.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import mate.academy.carsharing.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class FinePaymentService implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(2);
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long days) {
        BigDecimal daysBD = BigDecimal.valueOf(days);

        BigDecimal amount = dailyFee
                .multiply(daysBD)
                .multiply(FINE_MULTIPLIER)
                .multiply(BigDecimal.valueOf(100));

        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public boolean isApplicable(Payment.Type type) {
        return type == Payment.Type.FINE;
    }
}
