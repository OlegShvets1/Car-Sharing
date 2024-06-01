package mate.academy.carsharing.service.notification;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.model.TelegramUserInfo;
import mate.academy.carsharing.notification.NotificationTelegramBot;
import mate.academy.carsharing.service.telegrambot.TelegramUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final NotificationTelegramBot telegramBot;
    private final TelegramUserService telegramUserService;

    @Override
    public void onRentalCreationNotification(RentalResponseDto rental) {
        Long userId = rental.getUserId();

        String message = "--- NEW RENTAL ---\n"
                + "\nID: " + rental.getId()
                + "\nUser ID: " + userId
                + "\nCar ID: " + rental.getCarId()
                + "\nRental Date: " + rental.getRentalDate()
                + "\nReturn Date: " + rental.getRequiredReturnDate();
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void scheduledOverdueRentalNotification(Map<Long,
            List<RentalResponseDto>> overdueRentals) {
        for (Map.Entry<Long, List<RentalResponseDto>> overdueRental :
                overdueRentals.entrySet()) {
            StringBuilder message = new StringBuilder();

            if (overdueRental.getValue().isEmpty()) {
                message.append("--- NO OVERDUE RENTALS TODAY ---");
            } else {
                message.append("--- NEAREST OVERDUE RENTALS ---\n");
                for (RentalResponseDto rental : overdueRental.getValue()) {
                    message.append("\nID: ").append(rental.getId());
                    message.append("\nUser ID: ").append(rental.getUserId());
                    message.append("\nCar ID: ").append(rental.getCarId());
                    message.append("\nRental Date: ").append(rental.getRentalDate());
                    message.append("\nReturn Date: ").append(rental.getRequiredReturnDate())
                            .append("\n");
                }
            }

            message.append("\nYou will be FINED for overdue rentals. "
                    + "You will be charged an additional fee for each penalty day!");

            Long userId = overdueRental.getKey();
            telegramBot.sendMessage(message.toString(), getChatIdByUser(userId));
        }
    }

    @Override
    public void onPaymentCreationNotification(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = "--- NEW PAYMENT ---\n"
                + "\nID: " + paymentResponseDto.getId()
                + "\nFor: rental with id: " + paymentResponseDto.getRentalId()
                + "\nStatus: " + paymentResponseDto.getStatus()
                + "\nType: " + paymentResponseDto.getType()
                + "\nLink to pay: " + paymentResponseDto.getSessionUrl()
                + "\nAmount To Pay: " + paymentResponseDto.getAmountToPay();
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    @Override
    public void onSuccessfulPayment(PaymentResponseDto paymentResponseDto, Long userId) {
        String message = "Payment with id: " + paymentResponseDto.getId() + " paid successfully!";
        telegramBot.sendMessage(message, getChatIdByUser(userId));
    }

    private String getChatIdByUser(Long userId) {
        TelegramUserInfo telegramUserInfo = telegramUserService.findByUserId(userId);
        return telegramUserInfo == null ? null : telegramUserInfo.getChatId();
    }
}
