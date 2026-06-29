package com.sentinel.rulesengine.infrastructure.web.dto;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.model.AlertSeverity;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        UUID ruleId,
        UUID sourceId,
        double value,
        AlertSeverity severity,
        Instant triggeredAt,
        String message
) {
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.id(),
                alert.ruleId(),
                alert.sourceId(),
                alert.value(),
                alert.severity(),
                alert.triggeredAt(),
                alert.message()
        );
    }
}
