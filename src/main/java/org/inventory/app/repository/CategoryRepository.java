package org.inventory.app.repository;

import org.inventory.app.dto.CategoryStatsDTO;
import org.inventory.app.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findCategoryByName(String name);

    Optional<Category> findByName(String name);

    long count();

    @Query(value = "SELECT new org.inventory.app.dto.CategoryStatsDTO(" +
            "c.id, c.name,COUNT(DISTINCT p.brand.id), " +
            "COUNT(p.id),COALESCE(SUM(s.quantity), 0)) " +
            "FROM categories c " +
            "LEFT JOIN products p ON p.category.id = c.id " +
            "LEFT JOIN stock s ON s.product.id = p.id " +
            "GROUP BY c.id, c.name",
            countQuery = "SELECT COUNT(c.id) FROM categories c")
    Page<CategoryStatsDTO> findCategoryStats(Pageable pageable);
}
