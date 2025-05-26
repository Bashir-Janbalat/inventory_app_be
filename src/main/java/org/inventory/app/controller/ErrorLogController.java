package org.inventory.app.controller;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.ErrorLogDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.service.ErrorLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DEVELOPER')")
public class ErrorLogController {

    private final ErrorLogService errorLogService;


    @GetMapping
    public ResponseEntity<PagedResponseDTO<ErrorLogDTO>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String errorType,
            @RequestParam(required = false) String pathContains,
            @RequestParam(required = false) String messageContains,
            @RequestParam(required = false) Boolean resolved
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        PagedResponseDTO<ErrorLogDTO> result = errorLogService.getFilteredLogs(
                startDate, endDate, status, errorType, pathContains, messageContains,resolved, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Void> markAsResolved(@PathVariable Long id) {
        errorLogService.markAsResolved(id);
        return ResponseEntity.noContent().build();
    }
}
