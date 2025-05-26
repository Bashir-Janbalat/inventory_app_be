package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.ErrorLogDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ErrorLogMapper;
import org.inventory.app.model.ErrorLog;
import org.inventory.app.repository.ErrorLogRepository;
import org.inventory.app.service.ErrorLogService;
import org.inventory.app.specification.ErrorLogSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository errorLogRepository;
    private final ErrorLogMapper errorLogMapper;

    @Override
    public void save(ErrorLog log) {
        errorLogRepository.save(log);
    }

    @Override
    public PagedResponseDTO<ErrorLogDTO> getFilteredLogs(String startDate, String endDate,
                                                         Integer status, String errorType,
                                                         String pathContains, String messageContains,
                                                         Boolean resolved,
                                                         Pageable pageable) {

        Specification<ErrorLog> spec = Specification.where(ErrorLogSpecifications.hasStatus(status))
                .and(ErrorLogSpecifications.hasErrorType(errorType))
                .and(ErrorLogSpecifications.pathContains(pathContains))
                .and(ErrorLogSpecifications.messageContains(messageContains))
                .and(ErrorLogSpecifications.timestampAfter(startDate))
                .and(ErrorLogSpecifications.timestampBefore(endDate))
                .and(ErrorLogSpecifications.isResolved(resolved));

        Page<ErrorLog> page = errorLogRepository.findAll(spec, pageable);


        Page<ErrorLogDTO> dtoPage = page.map(errorLogMapper::toDto);

        return new PagedResponseDTO<>(dtoPage);
    }

    @Override
    public void markAsResolved(Long id) {
        ErrorLog log = errorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error log not found with ID: " + id));

        if (Boolean.TRUE.equals(log.getResolved())) return;

        log.setResolved(true);
        log.setResolvedAt(LocalDateTime.now());
        errorLogRepository.save(log);
    }
}
