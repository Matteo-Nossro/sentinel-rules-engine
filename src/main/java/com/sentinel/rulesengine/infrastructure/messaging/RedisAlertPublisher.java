package com.sentinel.rulesengine.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.port.out.AlertEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisAlertPublisher implements AlertEventPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisAlertPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Alert alert) {
        try {
            String json = objectMapper.writeValueAsString(alert);
            redisTemplate.convertAndSend("alert.triggered", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize alert for publishing", e);
        }
    }
}
