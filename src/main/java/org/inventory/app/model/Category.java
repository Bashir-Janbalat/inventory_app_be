package org.inventory.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity( name = "categories")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(unique = true)
    @NotBlank
    private String name;

    public Category(String name) {
        this.name = name;
    }
}