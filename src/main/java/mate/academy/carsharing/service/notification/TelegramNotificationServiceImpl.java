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

        String message = "* CONGRATULATIONS, THE NEW RENTAL WAS SUCCESSFULLY CREATED *\n"
                + "\nRental ID - " + rental.getId()
                + "\nUser ID - " + userId
                + "\nCar ID - " + rental.getCarId()
                + "\nRental Date - " + rental.getRentalDate()
                + "\nReturn Date - " + rental.getRequiredReturnDate();
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void paymentCreationNotification(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = "* CONGRATULATIONS, THE NEW PAYMENT WAS SUCCESSFULLY CREATED *\n"
                + "\nPayment ID - " + paymentResponseDto.getId()
                + "\nFor rental with id - " + paymentResponseDto.getRentalId()
                + "\nStatus - " + paymentResponseDto.getStatus()
                + "\nType - " + paymentResponseDto.getType()
                + "\nLink to pay - " + paymentResponseDto.getSessionUrl()
                + "\nAmount To Pay - " + paymentResponseDto.getAmountToPay();
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void successfulPaymentNotification(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = "* CONGRATULATIONS, PAYMENT WITH ID - "
                + paymentResponseDto.getId() + " PAID SUCCESSFULLY! "
                + "THANK YOU FOR USING OUR CAR_SHARING SERVICE. *";
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    private String getChatIdByUser(Long userId) {
        TelegramUserInfo telegramUserInfo = telegramUserService.findByUserId(userId);
        return telegramUserInfo == null ? null : telegramUserInfo.getChatId();
    }
}
