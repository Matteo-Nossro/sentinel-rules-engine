package com.sentinel.rulesengine.domain.model;

import java.util.UUID;
import java.time.Duration;

public record Rule(UUID id, String name, UUID sourceId, String metricName, RuleType type, ComparisonOperator operator, Double threshold, Duration windowSize, Integer frequency, AlertSeverity severity, boolean active) {

}