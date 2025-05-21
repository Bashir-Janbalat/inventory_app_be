package org.inventory.app.controller;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.enums.MovementType;
import org.inventory.app.projection.StockMovementSummaryDTO;
import org.inventory.app.service.StockMovementService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/stockMovements")
@RequiredArgsConstructor
public class StockMovementController {


    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<StockMovementSummaryDTO>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                            @RequestParam(defaultValue = "asc") String sortDirection,
                                                                            @RequestParam(required = false) LocalDate date,
                                                                            @RequestParam(required = false) MovementType movementType) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (date != null) {
            start = date.atStartOfDay();
            end = date.plusDays(1).atStartOfDay().minusNanos(1);
        }
        PagedResponseDTO<StockMovementSummaryDTO> movementDTOS =
                stockMovementService.getStockMovements(pageable, start, end, movementType);
        return ResponseEntity.ok(movementDTOS);
    }
}
