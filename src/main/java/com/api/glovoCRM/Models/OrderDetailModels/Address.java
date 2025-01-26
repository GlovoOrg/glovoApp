package com.api.glovoCRM.Models.OrderDetailModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address extends BaseEntity {

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 500, message = "Максимальная длина адреса — 500 символов")
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Min(value = -90, message = "Широта должна быть в диапазоне от -90 до 90")
    @Max(value = 90, message = "Широта должна быть в диапазоне от -90 до 90")
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Min(value = -180, message = "Долгота должна быть в диапазоне от -180 до 180")
    @Max(value = 180, message = "Долгота должна быть в диапазоне от -180 до 180")
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @OneToOne
    @JoinColumn(name = "order_id")
    @NotNull(message = "Заказ обязателен")
    private Order order;

}
