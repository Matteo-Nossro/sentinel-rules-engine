package com.sentinel.rulesengine.domain.port.in;

import com.sentinel.rulesengine.domain.model.Alert;

import java.util.UUID;

public interface AcknowledgeAlertUseCase {

    Alert acknowledge(UUID id);
}
