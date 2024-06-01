package mate.academy.carsharing.service.notification;

import java.util.List;
import java.util.Map;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;

public interface TelegramNotificationService {
    void onRentalCreationNotification(RentalResponseDto rentalResponseDto);

    void scheduledOverdueRentalNotification(Map<Long, List<RentalResponseDto>> overdueRentals);

    void onPaymentCreationNotification(PaymentResponseDto paymentResponseDto, Long userId);

    void onSuccessfulPayment(PaymentResponseDto paymentResponseDto, Long userId);
}
