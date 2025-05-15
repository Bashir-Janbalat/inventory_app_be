package org.inventory.app.repository;

import org.inventory.app.model.Warehouse;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {


    @Query("""
        SELECT new org.inventory.app.projection.WarehouseStatsDTO(
            w.id,
            w.name,
            w.address,
            COUNT(DISTINCT s.product),
            COALESCE(SUM(s.quantity), 0)
        )
        FROM warehouses w
        LEFT JOIN w.stocks s
        GROUP BY w.id, w.name, w.address
        """)
    Page<WarehouseStatsDTO> fetchWarehouseStatsWithTotalQuantity(Pageable pageable);
}
