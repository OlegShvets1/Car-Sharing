package mate.academy.carsharing.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BotRegister {
    private final NotificationTelegramBot notificationTelegramBot;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(notificationTelegramBot);
        } catch (TelegramApiException e) {
            System.err.println("Error registering bot: " + e.getMessage());
        }
    }
}