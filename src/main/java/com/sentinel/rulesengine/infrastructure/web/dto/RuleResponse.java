package com.sentinel.rulesengine.infrastructure.web.dto;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;

import java.util.UUID;

public record RuleResponse(
        UUID id,
        String name,
        UUID sourceId,
        String metricName,
        RuleType type,
        Double threshold,
        Long windowSizeSeconds,
        Integer frequency,
        AlertSeverity severity,
        boolean active
) {
    public static RuleResponse from(Rule rule) {
        return new RuleResponse(
                rule.id(),
                rule.name(),
                rule.sourceId(),
                rule.metricName(),
                rule.type(),
                rule.threshold(),
                rule.windowSize() != null ? rule.windowSize().getSeconds() : null,
                rule.frequency(),
                rule.severity(),
                rule.active()
        );
    }
}
