package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.inventory.app.enums.PurchaseStatus;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "purchases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Purchase extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();
}