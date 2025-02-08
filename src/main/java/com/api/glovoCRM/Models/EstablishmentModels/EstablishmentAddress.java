package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "establishment_adresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentAddress extends BaseEntity {

    @NotNull(message = "Адрес обязателен")
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 700, message = "Максимальная длина адреса — 700 символов")
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
    @JoinColumn(name = "establishment_id")
    @NotNull(message = "Заведение обязательно")
    private Establishment establishment;

    @Transient
    private Long establishmentId;

    @PrePersist
    private void prePersist() {
        if(establishment == null && this.establishmentId != null) {
            Establishment establishment = new Establishment();
            establishment.setId(this.establishmentId);
            this.establishment = establishment;
        }
    }


}
