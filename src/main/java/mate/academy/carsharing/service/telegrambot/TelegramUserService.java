package mate.academy.carsharing.service.telegrambot;

import mate.academy.carsharing.model.TelegramUserInfo;

public interface TelegramUserService {

    TelegramUserInfo save(String email, String chatId);

    TelegramUserInfo findByChatId(String chatId);

    TelegramUserInfo findByUserId(Long userId);
}
