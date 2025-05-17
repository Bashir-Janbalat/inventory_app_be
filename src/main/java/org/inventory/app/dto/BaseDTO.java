package org.inventory.app.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseDTO implements Serializable {

    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
