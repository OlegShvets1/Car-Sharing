package mate.academy.carsharing.notification;

import jakarta.transaction.Transactional;
import java.util.List;
import mate.academy.carsharing.exception.TelegramBotMessageException;
import mate.academy.carsharing.model.TelegramUserInfo;
import mate.academy.carsharing.repository.telegram.TelegramUserRepository;
import mate.academy.carsharing.service.telegrambot.TelegramUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NotificationTelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.key}")
    private String botToken;

    private final TelegramUserService telegramUserService;
    private final TelegramUserRepository telegramUserRepository;

    public NotificationTelegramBot(@Value("${bot.key}") String botToken,
                                   TelegramUserService telegramUserService,
                                   TelegramUserRepository telegramUserRepository) {
        this.botToken = botToken;
        this.telegramUserService = telegramUserService;
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Transactional
    @Override
    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String messageText = update.getMessage().getText();

        System.out.println("Received message from chatId - " + chatId + " is: " + "''"
                + messageText + "''");

        List<TelegramUserInfo> userInfoList = telegramUserRepository.findByChatIdWithRoles(chatId);

        if (!userInfoList.isEmpty()) {
            telegramUserRepository.deleteByChatId(chatId);
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                // Відправляємо стартове повідомлення і додаємо новий запис
                startingMessage(chatId);
            } else {
                validateUser(message, chatId);
            }
        }
    }

    private void startingMessage(String chatId) {
        String message = """
                This bot will inform you about:
                * Rentals creation
                * Payments creation
                * Successful payments
                * Overdue rentals

                Please, enter your e-mail to authorize""";
        sendMessage(message, chatId);
        TelegramUserInfo newUserInfo = new TelegramUserInfo();
        newUserInfo.setChatId(chatId);
        telegramUserRepository.save(newUserInfo);
    }

    private void validateUser(String email, String chatId) {
        try {
            telegramUserService.save(email, chatId);
            sendMessage("Congratulations, the authorization was successful!", chatId);
        } catch (Exception e) {
            sendMessage("To start authorization, enter '/start'. ", chatId);
        }
    }

    public void sendMessage(String value, String chatId) {
        if (chatId == null) {
            return;
        }

        SendMessage message = new SendMessage(chatId, value);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotMessageException("Couldn't send a message: " + value, e);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
