package com.api.glovoCRM.Models.EstablishmentModels;

import com.api.glovoCRM.Models.BaseEntity;
import com.api.glovoCRM.constants.EntityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image extends BaseEntity {

    @NotNull(message = "Изображение категории не может быть null")
    @NotBlank(message = "Изображение категории не может быть пустым")
    @Size(max = 800, message = "Максимальная длина URL изображения — 800 символов")
    @Column(length = 800, nullable = false)
    String url;

    @NotNull(message = "имя файла не может быть null")
    @NotBlank(message = "имя файла не может быть пустым")
    @Size(max = 200, message = "Максимальная длина названия файла внутри minio - 200 символов")
    @Column(name = "filename", nullable = false, length = 200)
    String filename; // file name inside minio


    @Column(name = "size", nullable = false)
    Long size; // size in kb

    @NotNull(message = "Не может быть null")
    @NotBlank(message = "Не может быть false")
    @Column(name = "contentType", nullable = false)
    private String contentType;

    @NotNull(message = "bucket name cannot be null")
    @NotBlank(message = "bucket не может быть пустым")
    @Column(name = "bucket", nullable = false)
    String bucket;

    @NotNull(message = "оригинальное name cannot be    null")
    @NotBlank(message = "оригинальное имя не может быть пустым")
    @Column(name = "original_filename", nullable = false)
    String originalFilename;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImageAssociation imageAssociation;
}
