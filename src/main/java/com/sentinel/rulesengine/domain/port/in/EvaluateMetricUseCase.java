package com.sentinel.rulesengine.domain.port.in;

import java.util.UUID;

public interface EvaluateMetricUseCase {
    void evaluate(UUID sourceId, String metricName, double value);
}
