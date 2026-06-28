package com.sentinel.rulesengine.domain.port.out;

import com.sentinel.rulesengine.domain.model.Alert;

import java.util.List;

public interface AlertRepository {
    Alert save(Alert alert);
    List<Alert> findAll();
}
