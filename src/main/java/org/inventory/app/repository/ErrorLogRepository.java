package org.inventory.app.repository;

import org.inventory.app.model.ErrorLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>, JpaSpecificationExecutor<ErrorLog> {

    @Query("SELECT e FROM ErrorLog e ORDER BY e.id ASC")
    List<ErrorLog> findOldestLogs(Pageable pageable);

    long count();
}
