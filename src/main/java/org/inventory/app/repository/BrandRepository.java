package org.inventory.app.repository;

import jakarta.validation.constraints.NotBlank;
import org.inventory.app.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {


    Optional<Brand> findByName(@NotBlank(message = "Brand name is required") String name);
}
