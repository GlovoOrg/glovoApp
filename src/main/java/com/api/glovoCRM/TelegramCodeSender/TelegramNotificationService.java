package com.api.glovoCRM.TelegramCodeSender;

import com.api.glovoCRM.Repositories.UserDAOs.UserRepository;
import com.api.glovoCRM.Exceptions.Telegram.AccountNotBoundEx;
import com.api.glovoCRM.Services.AuthServices.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
@Slf4j
public class TelegramNotificationService {
    @Value ("${admin.chat.id}")
    private String adminChatId;
    private final NotifyAdminByEmail notifyAdminByEmail;
    private final TelegramCodeSenderBot telegramBot;
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public TelegramNotificationService(NotifyAdminByEmail notifyAdminByEmail, TelegramCodeSenderBot telegramBot, UserRepository userRepository, VerificationCodeService verificationCodeService) {
        this.notifyAdminByEmail = notifyAdminByEmail;
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.verificationCodeService = verificationCodeService;
    }

    public void sendLoginCode(String username, String code) {
        String chatId = getChatIdByUsername(username);

        if (chatId == null) {
            throw new AccountNotBoundEx(
                    "Telegram не привязан. Отправьте /start в боте для инструкций"
            );
        }
        try {
            telegramBot.sendMessage(chatId, "⚽🔑⚽ Ваш код доступа: " + code);
            verificationCodeService.saveAuthCode(username, code);
        } catch (TelegramApiException e) {
            handleSendError(chatId, e);
            throw new UnableToSendNotificationException("Не удалось отправить код");
        }
    }

    @Cacheable(value = "telegramChatIds", key = "#username")
    public String getChatIdByUsername(String username) {
        return userRepository.findChatIdByLogin(username)
                .orElseThrow(() -> new AccountNotBoundEx("Аккаунт не привязан"));
    }

    @CacheEvict (value = "telegramChatIds", key = "#username")
    public void clearChatId(String username) {
        userRepository.clearTelegramChatId(username);
    }

    private void handleSendError(String chatId, TelegramApiException e) {
        if (e.getMessage().contains("chat not found")) {
            log.warn("Чат {} не найден, очищаем привязку", chatId);
            userRepository.findByChatId(chatId).ifPresent(user ->
                    clearChatId(user.getLogin())
            );
        }
        notifyAdmin("Ошибка отправки: " + e.getMessage());
    }

    private void notifyAdmin(String message) {
        try {
            telegramBot.sendMessage(adminChatId, "⚽⚠️⚽ " + message);
        } catch (TelegramApiException e) {
            log.error("Ошибка уведомления администратора: {}", e.getMessage());
            notifyAdminByEmail.notifyAdminByEmail("Ошибка Telegram: " + e.getMessage());
        }
    }
}