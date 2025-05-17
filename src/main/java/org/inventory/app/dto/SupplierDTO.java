package org.inventory.app.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierDTO extends BaseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String contactEmail;
    private String phone;
    private String address;

    public SupplierDTO(String name, String contactEmail) {
        this.name = name;
        this.contactEmail = contactEmail;
    }
}
