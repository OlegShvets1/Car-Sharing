package mate.academy.carsharing.service.payment.strategy;

import java.math.BigDecimal;
import mate.academy.carsharing.model.Payment;

public interface PaymentService {
    BigDecimal calculateAmount(BigDecimal dailyFee, long days);

    boolean isApplicable(Payment.Type type);
}
