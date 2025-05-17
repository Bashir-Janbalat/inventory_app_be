package org.inventory.app.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "brands")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Brand extends BaseEntity {

    @Column(unique = true)
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "brand")
    private List<Product> products = new ArrayList<>();

    public Brand(String name) {
        this.name = name;
    }
}