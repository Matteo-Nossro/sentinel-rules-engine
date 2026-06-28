package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.in.EvaluateMetricUseCase;
import com.sentinel.rulesengine.domain.port.out.AlertEventPublisher;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import com.sentinel.rulesengine.domain.service.RuleEvaluationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EvaluateMetricHandler implements EvaluateMetricUseCase {

    private final RuleRepository ruleRepository;
    private final AlertRepository alertRepository;
    private final AlertEventPublisher alertEventPublisher;
    private final RuleEvaluationService evaluationService;

    public EvaluateMetricHandler(RuleRepository ruleRepository,
                                  AlertRepository alertRepository,
                                  AlertEventPublisher alertEventPublisher,
                                  RuleEvaluationService evaluationService) {
        this.ruleRepository = ruleRepository;
        this.alertRepository = alertRepository;
        this.alertEventPublisher = alertEventPublisher;
        this.evaluationService = evaluationService;
    }

    @Override
    public void evaluate(UUID sourceId, String metricName, double value) {
        List<Rule> activeRules = ruleRepository.findActiveBySourceAndMetric(sourceId, metricName);

        for (Rule rule : activeRules) {
            if (evaluationService.evaluate(rule, value)) {
                String message = String.format("Rule '%s' triggered: %.2f > %.2f", rule.name(), value, rule.threshold());
                Alert alert = new Alert(UUID.randomUUID(), rule.id(), sourceId, value, rule.severity(), Instant.now(), message);
                alertRepository.save(alert);
                alertEventPublisher.publish(alert);
            }
        }
    }
}
