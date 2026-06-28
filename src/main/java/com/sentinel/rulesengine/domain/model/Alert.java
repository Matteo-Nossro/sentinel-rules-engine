package com.sentinel.rulesengine.domain.model;

import java.util.UUID;
import java.time.Instant;

public record Alert(UUID id, UUID ruleId, UUID sourceId, double value, AlertSeverity severity, Instant triggeredAt, String message) {
    
}
