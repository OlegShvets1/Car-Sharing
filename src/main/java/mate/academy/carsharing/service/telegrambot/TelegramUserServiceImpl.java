package mate.academy.carsharing.service.telegrambot;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.model.TelegramUserInfo;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.telegram.TelegramUserRepository;
import mate.academy.carsharing.security.CustomUserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public TelegramUserInfo save(String email, String chatId) {
        TelegramUserInfo telegramUserInfo = new TelegramUserInfo();
        telegramUserInfo.setUser((User) userDetailsService.loadUserByUsername(email));
        telegramUserInfo.setChatId(chatId);
        return telegramUserRepository.save(telegramUserInfo);
    }

    @Override
    public TelegramUserInfo findByChatId(String chatId) {
        return telegramUserRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find telegram info "
                        + "about user by chat id: " + chatId));
    }

    @Override
    public TelegramUserInfo findByUserId(Long userId) {
        return telegramUserRepository.findByUserId(userId)
                .orElse(null);
    }
}
