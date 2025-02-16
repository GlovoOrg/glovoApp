package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Embeddable.EstablishmentDetails;
import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Slf4j
@Entity
@Table(name = "establishments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Establishment extends BaseEntity {

    @NotNull(message = "Название заведения обязательно")
    @NotBlank(message = "Название заведения не может быть пустым")
    @Size(max = 455, message = "Максимальная длина названия — 455 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 50, message = "Минимальная стоимость доставки 50")
    @PositiveOrZero(message = "Стоимость доставки не может быть отрицательной")
    @Column(name = "price_of_delivery", nullable = false)
    private double priceOfDelivery;

    @Column(name = "total_rating", nullable = false)
    private double totalRating;

    @Min(value = 0, message = "Количество оценок не может быть отрицательным")
    @Max(value = 500, message = "Максимальное количество оценок — 500")
    @Column(name = "quantity_of_ratings", nullable = false)
    private int quantityOfRatings;

    @Min(value = 10, message = "Время доставки не может быть меньше 10 минут")
    @Max(value = 60, message = "Время доставки не может быть больше 60 минут")
    @Positive(message = "Время доставки не может быть отрицательным")
    @Column(name = "time_of_delivery", nullable = false)
    private int timeOfDelivery;

    @NotNull(message = "Время открытия обязательно")
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @NotNull(message = "Время закрытия обязательно")
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name asc")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name asc")
    private List<EstablishmentFilter> establishment_filters = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    @NotNull(message = "Подкатегория обязательна")
    private SubCategory subcategory;

    @OneToOne(mappedBy = "establishment", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstablishmentAddress establishmentAddress;

    @Embedded
    private EstablishmentDetails details;

    public boolean getIsOpen() {
        LocalTime now = LocalTime.now();
        return now.isAfter(this.openTime) && now.isBefore(this.closeTime);
    }
    public double getRating() {
        if (quantityOfRatings == 0) {
            return 0;
        }
        return Math.round((totalRating / quantityOfRatings) * 100.0) / 100.0;
    }
    public void addRating(int newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Рейтинг должен быть от 1 до 5");
        }
        this.totalRating += newRating;
        this.quantityOfRatings++;
    }
    @PreRemove
    private void preRemove() {
        log.debug("Вызов @PreRemove для заведения ID: {}", this.getId());
        if (subcategory != null) {
            subcategory.getEstablishments().remove(this);
            log.debug("Заведение удалено из подкатегории ID: {}", subcategory.getId());
        }
    }
}
