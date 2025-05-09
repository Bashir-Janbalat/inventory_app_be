package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String contactEmail;
    private String phone;
    private String address;

    public SupplierDTO(String name, String contactEmail) {
        this.name = name;
        this.contactEmail = contactEmail;
    }
}
