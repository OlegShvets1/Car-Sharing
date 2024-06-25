package mate.academy.carsharing.repository.telegram;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.model.TelegramUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelegramUserRepository extends JpaRepository<TelegramUserInfo, Long> {
    List<TelegramUserInfo> findByChatId(String chatId);

    Optional<TelegramUserInfo> findByUserId(Long userId);

    @Query("SELECT tui FROM TelegramUserInfo tui JOIN FETCH "
            + "tui.user u LEFT JOIN FETCH u.roles WHERE tui.chatId = :chatId")
    List<TelegramUserInfo> findByChatIdWithRoles(@Param("chatId") String chatId);

    @Transactional
    void deleteByChatId(String chatId);

}
