package org.inventory.app.repository;

import org.inventory.app.enums.MovementType;
import org.inventory.app.model.StockMovement;
import org.inventory.app.projection.StockMovementSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByWarehouseId(Long warehouseId);

    List<StockMovement> findByProductIdAndWarehouseId(Long productId, Long warehouseId);


    @Query("""
                SELECT
                    sm.id AS id,
                    p.id AS productId,
                    p.name AS productName,
                    w.id AS warehouseId,
                    w.name AS warehouseName,
                    sm.quantity AS quantity,
                    sm.movementType AS movementType,
                    sm.reason AS reason,
                    sm.createdAt AS createdAt,
                    sm.username AS username,
                    sm.productDeleted AS productDeleted,
                    sm.productNameSnapshot AS productNameSnapshot
                FROM stock_movements sm
                LEFT JOIN sm.product p
                LEFT JOIN sm.warehouse w
                WHERE (:movementType IS NULL OR
                       (:movementType = 'TRANSFER' AND sm.reason IN ('TRANSFERRED', 'RECEIVED_TRANSFER'))
                       OR (:movementType != 'TRANSFER' AND sm.movementType = :movementType))
                AND (:start IS NULL OR sm.createdAt >= :start)
                AND (:end IS NULL OR sm.createdAt <= :end)
            """)
    Page<StockMovementSummaryDTO> findAllProjected(Pageable pageable,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end, @Param("movementType") MovementType movementType);
}
