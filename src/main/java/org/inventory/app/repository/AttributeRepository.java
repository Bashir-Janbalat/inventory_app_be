package org.inventory.app.repository;

import org.inventory.app.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {


    Optional<Attribute> findFirstByName(String name);
}
