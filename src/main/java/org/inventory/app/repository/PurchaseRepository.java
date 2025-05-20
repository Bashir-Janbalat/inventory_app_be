package org.inventory.app.repository;

import org.inventory.app.model.Purchase;
import org.inventory.app.projection.PurchaseProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
                SELECT new org.inventory.app.projection.PurchaseProductDTO(p.id, p.name, p.costPrice, p.sku)
                FROM products p
                WHERE p.supplier.id = :supplierId
            """)
    List<PurchaseProductDTO> getProductsForSupplier(@Param("supplierId") Long supplierId);

    Page<Purchase> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
