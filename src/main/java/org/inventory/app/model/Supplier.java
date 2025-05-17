package org.inventory.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity( name = "suppliers")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "contactEmail"})
        }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Supplier extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String contactEmail;

    private String phone;

    private String address;

}