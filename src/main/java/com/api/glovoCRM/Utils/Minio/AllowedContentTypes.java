package com.api.glovoCRM.Utils.Minio;

import com.api.glovoCRM.constants.MimeType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedContentTypesValidator.class)
@Documented
public @interface AllowedContentTypes {
    MimeType[] value();

    String message() default "Недопустимый тип файла";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
