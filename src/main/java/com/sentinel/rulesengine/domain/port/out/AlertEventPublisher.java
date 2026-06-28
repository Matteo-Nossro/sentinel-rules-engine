package com.sentinel.rulesengine.domain.port.out;

import com.sentinel.rulesengine.domain.model.Alert;

public interface AlertEventPublisher {
    void publish(Alert alert);
}
