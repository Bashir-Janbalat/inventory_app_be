package org.inventory.app.repository;

import org.inventory.app.model.Brand;
import org.inventory.app.model.Category;
import org.inventory.app.model.Product;
import org.inventory.app.projection.MonthlyProductCountStatsDTO;
import org.inventory.app.projection.ProductStatusCountStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    Optional<Product> findBySku(String sku001);

    Page<Product> findByNameContainingIgnoreCase(String searchBy, Pageable pageable);

    Boolean existsByCategoryId(Long id);

    Boolean existsByBrandId(Long id);

    Boolean existsBySupplierId(Long id);

    @Query("SELECT new org.inventory.app.projection.ProductStatusCountStatsDTO(p.productStatus, COUNT(p)) FROM products p GROUP BY p.productStatus")
    List<ProductStatusCountStatsDTO> countProductsByStatus();

    @Query(value = """
            SELECT 
                DATE_FORMAT(p.created_at, '%Y-%m') AS month,
                COUNT(p.id) AS count
            FROM products p
            GROUP BY DATE_FORMAT(p.created_at, '%Y-%m')
            ORDER BY month""", nativeQuery = true)
    List<MonthlyProductCountStatsDTO> countProductsPerMonth();

    Page<Product> findByIsFeaturedTrue(Pageable pageable);
    @Query("""
                SELECT p FROM products p
                WHERE p.category = :category
                  AND p.id <> :excludedId
                  AND EXISTS (
                      SELECT s FROM stock s
                      WHERE s.product = p AND s.quantity > 0
                  )
            """)
    List<Product> findByCategoryAndIdNot(@Param("category") Category category,
                                         @Param("excludedId") Long excludedId,
                                         Pageable pageable);
    @Query("""
    SELECT p FROM products p
    WHERE p.brand = :brand
      AND p.id <> :excludedId
      AND EXISTS (
          SELECT s FROM stock s
          WHERE s.product = p AND s.quantity > 0
      )
""")
    List<Product> findByBrandAndIdNot(@Param("brand") Brand brand,
                                      @Param("excludedId") Long excludedId,
                                      Pageable pageable);
}
