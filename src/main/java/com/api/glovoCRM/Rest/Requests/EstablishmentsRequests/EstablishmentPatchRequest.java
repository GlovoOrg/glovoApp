package com.api.glovoCRM.Rest.Requests.EstablishmentsRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class EstablishmentPatchRequest extends BaseRequest {
    @Positive(message = "Id подкатегории должен быть положительным")
    private Long subCategoryId;

    @Min(value = 50, message = "Минимальная стоимость доставки 50")
    @PositiveOrZero(message = "Стоимость доставки не может быть отрицательной")
    private Double priceOfDelivery;

    @Min(value = 10, message = "Время доставки не может быть меньше 10 минут")
    @Max(value = 60, message = "Время доставки не может быть больше 60 минут")
    @Positive(message = "Время доставки должно быть положительным")
    private Integer timeOfDelivery;
    private LocalTime openTime;
    private LocalTime closeTime;
    @Length(max = 120)
    private String addressLine;
    @Min(value = -90, message = "Широта должна быть в диапазоне от -90 до 90")
    @Max(value = 90, message = "Широта должна быть в диапазоне от -90 до 90")
    private Double latitude;
    @Min(value = -180, message = "Долгота должна быть в диапазоне от -180 до 180")
    @Max(value = 180, message = "Долгота должна быть в диапазоне от -180 до 180")
    private Double longitude;

}
