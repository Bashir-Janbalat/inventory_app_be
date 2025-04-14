package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "images")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    private String altText;

}