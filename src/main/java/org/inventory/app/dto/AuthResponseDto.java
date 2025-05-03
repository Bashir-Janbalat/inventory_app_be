package org.inventory.app.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AuthResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String accessToken;

}
