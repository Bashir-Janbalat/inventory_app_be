package org.inventory.app.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class LoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

}
