package com.api.glovoCRM.constants;


import lombok.Getter;

@Getter
public enum MimeType {
    JPEG("image/jpeg"),
    JPG("image/jpg"),
    PNG("image/png"),
    //GIF("image/gif"),
    //BMP("image/bmp"),
    SVG("image/svg+xml");
    //TIFF("image/tiff");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

}
