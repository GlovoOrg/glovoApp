package com.api.glovoCRM.Rest.Requests.EstablishmentsRequests;

import com.api.glovoCRM.Rest.Requests.BaseRequestNotNull;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;
@EqualsAndHashCode(callSuper = true)
@Data
public class EstablishmentUpdateRequest extends BaseRequestNotNull {
    @NotNull(message = "Id подкатегории должно быть обязательным")
    @Positive(message = "Id подкатегории должен быть положительным")
    private Long subCategoryId;

    @Min(value = 50, message = "Минимальная стоимость доставки 50")
    @PositiveOrZero(message = "Стоимость доставки не может быть отрицательной")
    private Double priceOfDelivery;

    @Min(value = 10, message = "Время доставки не может быть меньше 10 минут")
    @Max(value = 60, message = "Время доставки не может быть больше 60 минут")
    @Positive(message = "Время доставки должно быть положительным")
    private Integer timeOfDelivery;

    @NotNull(message = "Время открытия обязательно")
    private LocalTime openTime;

    @NotNull(message = "Время закрытия обязательно")
    private LocalTime closeTime;
    @NotBlank(message = "Адрес обязателен")
    private String addressLine;
    @Min(value = -90, message = "Широта должна быть в диапазоне от -90 до 90")
    @Max(value = 90, message = "Широта должна быть в диапазоне от -90 до 90")
    private Double latitude;
    @Min(value = -180, message = "Долгота должна быть в диапазоне от -180 до 180")
    @Max(value = 180, message = "Долгота должна быть в диапазоне от -180 до 180")
    private Double longitude;
}
