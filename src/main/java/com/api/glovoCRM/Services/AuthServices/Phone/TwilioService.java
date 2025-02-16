package com.api.glovoCRM.Services.AuthServices.Phone;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class TwilioService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;
    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 1000))
    public void sendSms(String toPhoneNumber, String code) {
        log.info("Происходит инициализация twilio");
        Twilio.init(accountSid, authToken);
        log.info("Отправка SMS на номер: {}", toPhoneNumber);
        Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                "Ваш код безопасности для сайта glovo: " + code
        ).create();
    }
    @Recover
    public void recoverSendSms(Exception e, String toPhoneNumber, String code) {
        log.error("Не удалось отправить смс после 4 попыток: {}", e.getMessage());
        throw new RuntimeException("Ошибка отправки SMS");
    }
}
