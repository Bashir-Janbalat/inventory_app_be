package org.inventory.app.repository;

import org.inventory.app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    Optional<Product> findBySku(String sku001);

    Page<Product> findByNameContainingIgnoreCase(String searchBy, Pageable pageable);

    Boolean existsByCategoryId(Long id);
}
