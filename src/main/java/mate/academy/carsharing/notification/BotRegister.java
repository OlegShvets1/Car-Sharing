package mate.academy.carsharing.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class BotRegister {
    private static final Logger logger = LoggerFactory.getLogger(BotRegister.class);

    private final NotificationTelegramBot notificationTelegramBot;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(notificationTelegramBot);
        } catch (TelegramApiException e) {
            logger.error("An error occurred while registering the bot: {}", e.getMessage(), e);
        }
    }
}
