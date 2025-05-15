package org.inventory.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementReason reason;

    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    private String username;

    @Column(name = "product_name_snapshot")
    private String productNameSnapshot;

    @Column(name = "product_deleted")
    private Boolean productDeleted = false;
}
