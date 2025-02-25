package com.api.glovoCRM.Services.OpenAIService;


import com.api.glovoCRM.Rest.Responses.OpenAIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Slf4j
@Service
public class OpenAIService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    /**
     * Отправляет запрос в OpenAI API и получает ответ.
     * @param userMessage сообщение пользователя
     * @return ответ от OpenAI или сообщение об ошибке
     */
    public String getGPTResponse(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String requestBody = "{"
                + "\"model\": \"gpt-4o-mini\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage.substring(0, Math.min(userMessage.length(), 1000)) + "\"}],"
                + "\"temperature\": 0.7"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        log.info("Отправка запроса в OpenAI API...");
        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, OpenAIResponse.class);
        log.info("Ответ получен от OpenAI API.");

        OpenAIResponse openAIResponse = response.getBody();
        //System.out.println("Ответ от API: " + response.getBody());

        if (openAIResponse != null) {
            if (openAIResponse.getError() != null && openAIResponse.getError().getCode() == 402) {
                return "Извините, но для выполнения этого запроса недостаточно кредитов. " +
                        "Пожалуйста, сократите длину сообщения или попробуйте позже.";
            }

            if (openAIResponse.getChoices() != null && !openAIResponse.getChoices().isEmpty()) {
                return openAIResponse.getChoices().get(0).getMessage().getContent();
            }
        }
        return "Ошибка: пустой или некорректный ответ от OpenAI.";
    }

    public String getRestaurantAdvice() {
        return "🍽 Попробуйте национальную кухню или уютное кафе рядом!";
    }

    public String getMarketAdvice() {
        return "🏪 В ближайших супермаркетах вы найдете всё необходимое.";
    }

    public String getFastFoodAdvice() {
        return "🍔 Захватите бургер или шаурму в одном из заведений поблизости!";
    }

}





