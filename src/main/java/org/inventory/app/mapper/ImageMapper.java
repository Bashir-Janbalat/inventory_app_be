package org.inventory.app.mapper;

import org.inventory.app.dto.ImageDTO;
import org.inventory.app.model.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDTO toDto(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setId( image.getId());
        dto.setImageUrl( image.getImageUrl());
        dto.setAltText( image.getAltText());
        return dto;
    }

    public Image toEntity(ImageDTO imageDTO) {
        Image imageEntity = new Image();
        imageEntity.setId(imageDTO.getId());
        imageEntity.setImageUrl(imageDTO.getImageUrl());
        imageEntity.setAltText(imageDTO.getAltText());
        return imageEntity;
    }
}
