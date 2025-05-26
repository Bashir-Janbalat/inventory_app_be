package org.inventory.app.specification;

import org.inventory.app.model.ErrorLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogSpecifications {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Specification<ErrorLog> hasStatus(Integer status) {
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<ErrorLog> hasErrorType(String errorType) {
        return (root, query, builder) -> {
            if (errorType == null || errorType.isEmpty()) return null;
            return builder.like(builder.lower(root.get("error")), "%" + errorType.toLowerCase() + "%");
        };
    }

    public static Specification<ErrorLog> pathContains(String path) {
        return (root, query, builder) -> {
            if (path == null || path.isEmpty()) return null;
            return builder.like(builder.lower(root.get("path")), "%" + path.toLowerCase() + "%");
        };
    }

    public static Specification<ErrorLog> messageContains(String message) {
        return (root, query, builder) -> {
            if (message == null || message.isEmpty()) return null;
            return builder.like(builder.lower(root.get("message")), "%" + message.toLowerCase() + "%");
        };
    }

    public static Specification<ErrorLog> timestampAfter(String startDate) {
        return (root, query, builder) -> {
            if (startDate == null || startDate.isEmpty()) return null;
            LocalDateTime date = LocalDateTime.parse(startDate, FORMATTER);
            return builder.greaterThanOrEqualTo(root.get("timestamp"), date);
        };
    }

    public static Specification<ErrorLog> timestampBefore(String endDate) {
        return (root, query, builder) -> {
            if (endDate == null || endDate.isEmpty()) return null;
            LocalDateTime date = LocalDateTime.parse(endDate, FORMATTER);
            return builder.lessThanOrEqualTo(root.get("timestamp"), date);
        };
    }

    public static Specification<ErrorLog> isResolved(Boolean resolved) {
        return (root, query, cb) -> resolved == null ? null : cb.equal(root.get("resolved"), resolved);
    }
}
