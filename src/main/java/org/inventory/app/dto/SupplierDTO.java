package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierDTO {

    private Long id;
    private String name;
    private String contactEmail;

    public SupplierDTO(String name, String contactEmail) {
        this.name = name;
        this.contactEmail = contactEmail;
    }
}
