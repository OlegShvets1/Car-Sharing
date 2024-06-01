package mate.academy.carsharing.repository.telegram;

import java.util.Optional;
import mate.academy.carsharing.model.TelegramUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUserInfo, Long> {
    Optional<TelegramUserInfo> findByChatId(String chatId);

    Optional<TelegramUserInfo> findByUserId(Long userId);
}
