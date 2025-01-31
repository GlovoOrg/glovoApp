package com.api.glovoCRM.Utils.Minio;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.api.glovoCRM.constants.MimeType;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllowedContentTypesValidator implements ConstraintValidator<AllowedContentTypes, MultipartFile> {
    private List<String> allowedPatterns;
    private final Tika tika = new Tika();

    @Override
    public void initialize(AllowedContentTypes constraint) {
        this.allowedPatterns = Arrays.stream(constraint.value())
                .map(MimeType::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        try (InputStream is = file.getInputStream()) {
            String detectedType = tika.detect(is);
            boolean isValid = allowedPatterns.stream()
                    .anyMatch(pattern -> matchesPattern(detectedType, pattern));

            if (!isValid) {
                context.disableDefaultConstraintViolation(); // Отключаем стандартное сообщение
                context.buildConstraintViolationWithTemplate("Недопустимый тип файла: " + detectedType)
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (IOException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ошибка чтения файла: " + e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }

    private boolean matchesPattern(String detectedType, String pattern) {
        // Тут, убираем параметры после ';' (например, "image/png; charset=UTF-8")
        String cleanDetectedType = detectedType.split(";")[0].trim();
        return cleanDetectedType.equalsIgnoreCase(pattern);
    }
}