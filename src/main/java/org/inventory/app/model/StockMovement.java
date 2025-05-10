package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;

@Entity(name = "stock_movements")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementReason reason;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id")
    private Product product;

    private String username;
}
