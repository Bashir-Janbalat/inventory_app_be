package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.inventory.app.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    private String name;

    @Column(unique = true)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;


    private BigDecimal costPrice;

    private BigDecimal sellingPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    private Supplier supplier;

    /* mappedBy = "product" – verweist auf das Feld product in der Image-Klasse */
    /* cascade = CascadeType.ALL – Änderungen am Produkt wirken sich auch auf Bilder aus */
    /* orphanRemoval = true – wenn ein Bild aus der Liste entfernt wird, wird es auch aus der DB gelöscht */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<StockMovement> stockMovements;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.INACTIVE;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @ManyToMany
    @JoinTable(
            name = "related_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "related_product_id")
    )
    private List<Product> relatedProducts = new ArrayList<>();
}