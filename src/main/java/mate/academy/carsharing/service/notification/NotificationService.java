package mate.academy.carsharing.service.notification;

import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;

public interface NotificationService {
    void rentalCreationNotification(RentalResponseDto rentalResponseDto);

    void paymentCreationNotification(PaymentResponseDto paymentResponseDto, Long userId);

    void successfulPaymentNotification(PaymentResponseDto paymentResponseDto, Long userId);
}
