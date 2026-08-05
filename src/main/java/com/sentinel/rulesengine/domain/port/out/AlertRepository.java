package com.sentinel.rulesengine.domain.port.out;

import com.sentinel.rulesengine.domain.model.Alert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository {
    Alert save(Alert alert);
    List<Alert> findAll();
    Optional<Alert> findById(UUID id);
}
