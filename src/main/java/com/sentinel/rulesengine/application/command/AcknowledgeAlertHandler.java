package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.port.in.AcknowledgeAlertUseCase;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AcknowledgeAlertHandler implements AcknowledgeAlertUseCase {

    private final AlertRepository alertRepository;

    public AcknowledgeAlertHandler(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Alert acknowledge(UUID id) {
        Alert existing = alertRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alert not found: " + id));

        Alert toSave = new Alert(existing.id(), existing.ruleId(), existing.sourceId(),
                existing.value(), existing.severity(), existing.triggeredAt(),
                existing.message(), true);

        return alertRepository.save(toSave);
    }
}
