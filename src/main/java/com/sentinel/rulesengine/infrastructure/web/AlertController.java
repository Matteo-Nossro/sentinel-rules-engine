package com.sentinel.rulesengine.infrastructure.web;

import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.infrastructure.web.dto.AlertResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<AlertResponse> getAll() {
        return alertRepository.findAll().stream().map(AlertResponse::from).toList();
    }
}
