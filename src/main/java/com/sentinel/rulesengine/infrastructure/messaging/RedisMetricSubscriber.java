package com.sentinel.rulesengine.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.rulesengine.domain.port.in.EvaluateMetricUseCase;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMetricSubscriber implements MessageListener {

    private final EvaluateMetricUseCase evaluateMetricUseCase;
    private final ObjectMapper objectMapper;

    public RedisMetricSubscriber(EvaluateMetricUseCase evaluateMetricUseCase, ObjectMapper objectMapper) {
        this.evaluateMetricUseCase = evaluateMetricUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MetricReceivedEvent event = objectMapper.readValue(message.getBody(), MetricReceivedEvent.class);
            evaluateMetricUseCase.evaluate(event.sourceId(), event.metricName(), event.value());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process MetricReceived event", e);
        }
    }
}
