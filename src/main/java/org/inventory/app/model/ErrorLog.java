package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String timestamp;
    private Integer status;
    private String error;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String path;

    @Lob
    private String stackTrace;

    @Builder.Default
    private Boolean resolved = false;

    private LocalDateTime resolvedAt;
}
