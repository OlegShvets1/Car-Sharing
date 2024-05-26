package mate.academy.carsharing.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;
import mate.academy.carsharing.model.Payment;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    private Payment.Status status;
    private Payment.Type type;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
