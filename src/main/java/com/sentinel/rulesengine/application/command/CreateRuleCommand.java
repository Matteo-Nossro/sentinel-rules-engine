package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.RuleType;

import java.time.Duration;
import java.util.UUID;

public record CreateRuleCommand(
        String name,
        UUID sourceId,
        String metricName,
        RuleType type,
        Double threshold,
        Duration windowSize,
        Integer frequency,
        AlertSeverity severity,
        boolean active
) {}
