package com.sentinel.rulesengine.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

public record MetricReceivedEvent(UUID sourceId, String metricName, double value, Instant timestamp) {}
