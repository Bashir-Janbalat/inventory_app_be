package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ErrorLogDTO implements Serializable {
    private Long id;
    private String timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private String stackTrace;
    private Boolean resolved;
    private String resolvedAt;
}