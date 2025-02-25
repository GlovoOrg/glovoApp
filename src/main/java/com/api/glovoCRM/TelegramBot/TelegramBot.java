package com.api.glovoCRM.TelegramBot;

import com.api.glovoCRM.Services.OpenAIService.OpenAIService;
import com.api.glovoCRM.Services.OpenAIService.PlacesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final OpenAIService openAIService;
    private final PlacesService placesService;

    public TelegramBot(OpenAIService openAIService, PlacesService placesService) {
        this.openAIService = openAIService;
        this.placesService = placesService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleTextMessage(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String messageText = update.getMessage().getText();

        log.info("Полученное сообщение: {}", messageText);

        if (messageText.equals("/start")) {
            sendCategoryMenu(chatId);
        } else {

            if (messageText.equals("Маркеты") || messageText.equals("Рестораны") || messageText.equals("Фаст-Фуд")) {
                sendCategoryMenu(chatId);
            } else {
                String response = openAIService.getGPTResponse(messageText);

                if (response == null || response.isEmpty()) {
                    response = "Я не знаю ответа на этот вопрос. Выберите категорию:";
                    sendCategoryMenu(chatId);
                }

                sendTextMessage(chatId, response, null);
            }
        }
    }

    private void handleCallbackQuery(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();

        if (callbackData.equals("category_markets")) {
            sendMarketList(chatId);
        } else if (callbackData.equals("category_restaurants")) {
            sendRestaurantsList(chatId);
        } else if (callbackData.equals("category_fast-food")) {
            sendFastFoodList(chatId);
        }
    }

    private void sendCategoryMenu(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите категорию:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton marketsButton = new InlineKeyboardButton("🏪 Маркеты");
        marketsButton.setCallbackData("category_markets");

        InlineKeyboardButton restaurantsButton = new InlineKeyboardButton("🍽 Рестораны");
        restaurantsButton.setCallbackData("category_restaurants");

        InlineKeyboardButton fastfoodsButton = new InlineKeyboardButton("🍔 Быстрое питание");
        fastfoodsButton.setCallbackData("category_fast-food");

        keyboard.add(List.of(marketsButton, restaurantsButton));
        keyboard.add(List.of(fastfoodsButton));

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        sendTextMessage(chatId, message.getText(), markup);
    }

    private void sendRestaurantsList(String chatId) {
        double userLatitude = 42.8667;
        double userLongitude = 74.5667;

        // Получаем данные из Google Places
        String response = placesService.getNearbyRestaurants(userLatitude, userLongitude);

        // Если Google Places не вернул данные, используем OpenAI
        if (response == null || response.isEmpty()) {
            response = openAIService.getRestaurantAdvice();
        }

        sendTextMessage(chatId, response, null);
    }

    private void sendMarketList(String chatId) {
        double userLatitude = 42.8773;
        double userLongitude = 74.5868;

        String response = placesService.getNearByMarkets(userLatitude, userLongitude);

        if (response == null || response.isEmpty()) {
            response = openAIService.getMarketAdvice();
        }

        sendTextMessage(chatId, response, null);
    }
    private void sendFastFoodList(String chatId) {
        double userLatitude = 42.8667;
        double userLongitude = 74.5667;

        String response = placesService.getNearByFastFood(userLatitude, userLongitude);

        if (response == null || response.isEmpty()) {
            response = openAIService.getFastFoodAdvice();
        }
        sendTextMessage(chatId, response, null);
    }

    private void sendTextMessage(String chatId, String message, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (message != null && !message.isEmpty()) {
            sendMessage.setText(message);
        } else {
            sendMessage.setText("Сообщение не может быть пустым");
        }

        if (markup != null) {
            sendMessage.setReplyMarkup(markup);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}