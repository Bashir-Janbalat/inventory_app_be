package org.inventory.app.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttributeDTO implements Serializable {

    private Long id;
    private String name;
}
