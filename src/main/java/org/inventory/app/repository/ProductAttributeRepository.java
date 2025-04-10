package org.inventory.app.repository;

import org.inventory.app.model.ProductAttribute;
import org.inventory.app.model.ProductAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, ProductAttributeId> {
}
