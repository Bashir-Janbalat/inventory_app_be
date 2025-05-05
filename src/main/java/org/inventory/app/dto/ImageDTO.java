package org.inventory.app.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String imageUrl;
    private String altText;

    public ImageDTO(String imageUrl, String altText) {
        this.imageUrl = imageUrl;
        this.altText = altText;
    }
}
