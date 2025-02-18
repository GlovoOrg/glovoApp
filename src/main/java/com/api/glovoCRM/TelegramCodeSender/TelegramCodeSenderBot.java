package com.api.glovoCRM.TelegramCodeSender;

import com.api.glovoCRM.DAOs.UserDAOs.UserDAO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class TelegramCodeSenderBot extends TelegramLongPollingBot {
    private static boolean isBotRunning = false;
    private final UserDAO userDAO;
    private static final String START_INSTRUCTIONS = """
        ⚽🔐⚽ Для привязки Telegram аккаунта:
        1. Войдите в основное приложение Glovo(регистрация)
        2. Отправьте команду: /bind ваш_логин
        Пример: /bind alinurchik""";

    @Value("${bot.send.code.token}")
    private String botToken;
    @PostConstruct
    public void init() {
        log.info("Инициализация Telegram бота...");
        synchronized (TelegramCodeSenderBot.class) {
            if (isBotRunning) {
                log.error("Бот уже запущен в другом экземпляре.");
                return;
            }
            isBotRunning = true;
        }

        if (botToken == null || botToken.isEmpty()) {
            log.error("Токен бота не задан! Проверьте application.properties.");
            return;
        }

        log.info("Токен бота загружен: {}", botToken);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Бот успешно зарегистрирован.");
        } catch (TelegramApiException e) {
            log.error("Ошибка при регистрации бота в Telegram API", e);
        }
    }
    @PreDestroy
    public void shutdown() {
        log.info("Остановка Telegram бота...");
        synchronized (TelegramCodeSenderBot.class) {
            isBotRunning = false;
        }
    }

    public TelegramCodeSenderBot(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public String getBotUsername() {
        return "StuffCodeSender";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Получено обновление: {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            handleMessage(chatId, messageText);
            log.info("Получено сообщение: {} от chatId: {}", messageText, chatId);
        }
    }
    public void handleStartCommand(String chatId) {
        try {
            sendMessage(chatId, START_INSTRUCTIONS);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения для chat_id {}: {}", chatId, e.getMessage());
        }
    }
    public void handleLoginBinding(String chatId, String login) {
        userDAO.findByLogin(login).ifPresentOrElse(
                user -> {
                    userDAO.updateTelegramChatId(login, chatId);
                    sendSuccessMessage(chatId, "✅ Аккаунт привязан к логину: " + login);
                },
                () -> sendErrorMessage(chatId, "❌ Пользователь с логином " + login + " не найден")
        );
    }
    public void handleMessage(String chatId, String messageText) {
        if (messageText.startsWith("/start")) {
            handleStartCommand(chatId);
            log.info("Получено сообщение: {} от chatId1: {}", messageText, chatId);
        } else if (messageText.startsWith("/bind ")) {
            String login = messageText.substring(6).trim();
            handleLoginBinding(chatId, login);
        }
    }
    public void sendMessage(String chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        execute(sendMessage);
    }
    private void sendSuccessMessage(String chatId, String message){
        try {
            sendMessage(chatId, message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения успех: {}", e.getMessage());
        }
    }
    private void sendErrorMessage(String chatId, String errorMessage) {
        try {
            sendMessage(chatId, errorMessage + "\n\n" + START_INSTRUCTIONS);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения ошибки: {}", e.getMessage());
        }
    }
}
