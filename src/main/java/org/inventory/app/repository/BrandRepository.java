package org.inventory.app.repository;

import jakarta.validation.constraints.NotBlank;
import org.inventory.app.projection.BrandStatsDTO;
import org.inventory.app.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {


    Optional<Brand> findByName(@NotBlank(message = "Brand name is required") String name);

    long count();

    @Query(value = "SELECT new org.inventory.app.projection.BrandStatsDTO(b.id, b.name, COUNT(DISTINCT p.id), COALESCE(SUM(s.quantity), 0)) " +
                    "FROM brands b " +
                    "LEFT JOIN products p ON b.id = p.brand.id " +
                    "LEFT JOIN stock s ON p.id = s.product.id " +
                    "GROUP BY b.id, b.name",
            countQuery = "SELECT COUNT(b.id) FROM brands b")
    Page<BrandStatsDTO> findBrandsWithStats(Pageable pageable);

    @Query("""
    SELECT DISTINCT b
    FROM brands b
    JOIN b.products p
    JOIN p.category c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchByCategory, '%'))
    """)
    Page<Brand> findByProductCategoryNameContainingIgnoreCase(@Param("searchByCategory") String searchByCategory, Pageable pageable);
}
