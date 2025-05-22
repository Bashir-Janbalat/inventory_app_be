package org.inventory.app.specification;

import jakarta.persistence.criteria.JoinType;
import org.inventory.app.enums.ProductStatus;
import org.inventory.app.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

    public static Specification<Product> hasNameLike(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + term.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null || categoryName.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(
                    root.join("category", JoinType.LEFT).get("name"),
                    categoryName
            );
        };
    }

    public static Specification<Product> hasBrand(String brandName) {
        return (root, query, cb) -> {
            if (brandName == null || brandName.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(
                    root.join("brand", JoinType.LEFT).get("name"),
                    brandName
            );
        };
    }

    public static Specification<Product> hasSupplier(String supplierName) {
        return (root, query, cb) -> {
            if (supplierName == null || supplierName.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(
                    root.join("supplier", JoinType.LEFT).get("name"),
                    supplierName
            );
        };
    }

    public static Specification<Product> hasStatus(ProductStatus productStatus) {
        return (root, query, cb) -> {
            if (productStatus == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("productStatus"), productStatus);
        };

    }
}
