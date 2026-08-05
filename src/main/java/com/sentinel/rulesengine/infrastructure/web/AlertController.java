package com.sentinel.rulesengine.infrastructure.web;

import com.sentinel.rulesengine.domain.port.in.AcknowledgeAlertUseCase;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.infrastructure.web.dto.AlertResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertRepository alertRepository;
    private final AcknowledgeAlertUseCase acknowledgeAlertUseCase;

    public AlertController(AlertRepository alertRepository, AcknowledgeAlertUseCase acknowledgeAlertUseCase) {
        this.alertRepository = alertRepository;
        this.acknowledgeAlertUseCase = acknowledgeAlertUseCase;
    }

    @GetMapping
    public List<AlertResponse> getAll() {
        return alertRepository.findAll().stream().map(AlertResponse::from).toList();
    }

    @PatchMapping("/{id}")
    public AlertResponse acknowledge(@PathVariable UUID id) {
        return AlertResponse.from(acknowledgeAlertUseCase.acknowledge(id));
    }
}
