package org.inventory.app.mapper;

import org.inventory.app.dto.ErrorLogDTO;
import org.inventory.app.model.ErrorLog;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ErrorLogMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ErrorLogDTO toDto(ErrorLog log) {
        if (log == null) {
            return null;
        }

        return ErrorLogDTO.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .status(log.getStatus())
                .error(log.getError())
                .message(log.getMessage())
                .path(log.getPath())
                .stackTrace(log.getStackTrace())
                .resolved(log.getResolved())
                .resolvedAt(log.getResolvedAt() != null ? log.getResolvedAt().format(FORMATTER) : null)
                .build();
    }
}