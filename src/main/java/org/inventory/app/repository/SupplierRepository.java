package org.inventory.app.repository;

import org.inventory.app.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String supplierName);

    Optional<Supplier> findSupplierByName(String name);
}