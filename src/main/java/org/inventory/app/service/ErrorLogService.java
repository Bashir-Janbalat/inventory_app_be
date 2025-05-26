package org.inventory.app.service;

import org.inventory.app.dto.ErrorLogDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.model.ErrorLog;
import org.springframework.data.domain.Pageable;

public interface ErrorLogService {

    void save(ErrorLog log);

    PagedResponseDTO<ErrorLogDTO> getFilteredLogs(
            String startDate,
            String endDate,
            Integer status,
            String errorType,
            String pathContains,
            String messageContains,
            Boolean resolved,
            Pageable pageable
    );

    void markAsResolved(Long id);
}