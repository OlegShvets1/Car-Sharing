package mate.academy.carsharing.service.notification;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.model.TelegramUserInfo;
import mate.academy.carsharing.notification.NotificationTelegramBot;
import mate.academy.carsharing.service.telegrambot.TelegramUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationService {
    private final NotificationTelegramBot telegramBot;
    private final TelegramUserService telegramUserService;

    @Override
    public void rentalCreationNotification(RentalResponseDto rental) {
        Long userId = rental.getUserId();
        String message = """
        Rental ID - %d
        User ID - %s
        Car ID - %d
        Rental Date - %s
        Return Date - %s
                """.formatted(rental.getId(), userId, rental.getCarId(),
                rental.getRentalDate(), rental.getRequiredReturnDate());
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void paymentCreationNotification(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = """
        * CONGRATULATIONS, THE NEW PAYMENT WAS SUCCESSFULLY CREATED *
        
        Payment ID - %s
        For rental with id - %s
        Status - %s
        Type - %s
        Link to pay - %s
        Amount To Pay - %s
                """.formatted(
                paymentResponseDto.getId(),
                paymentResponseDto.getRentalId(),
                paymentResponseDto.getStatus(),
                paymentResponseDto.getType(),
                paymentResponseDto.getSessionUrl(),
                paymentResponseDto.getAmountToPay()
        );
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void successfulPaymentNotification(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = """
        * CONGRATULATIONS, PAYMENT WITH ID - %s PAID SUCCESSFULLY! 
        THANK YOU FOR USING OUR CAR_SHARING SERVICE. *
                """.formatted(paymentResponseDto.getId());
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    private String getChatIdByUser(Long userId) {
        TelegramUserInfo telegramUserInfo = telegramUserService.findByUserId(userId);
        return telegramUserInfo == null ? null : telegramUserInfo.getChatId();
    }
}
