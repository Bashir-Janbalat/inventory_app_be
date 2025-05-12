package org.inventory.app.repository;

import org.inventory.app.model.Product;
import org.inventory.app.model.Stock;
import org.inventory.app.model.StockId;
import org.inventory.app.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {

    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);
}