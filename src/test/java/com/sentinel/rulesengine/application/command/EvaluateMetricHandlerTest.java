package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import com.sentinel.rulesengine.domain.port.out.AlertEventPublisher;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import com.sentinel.rulesengine.domain.service.RuleEvaluationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluateMetricHandlerTest {

    @Mock
    private RuleRepository ruleRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private AlertEventPublisher alertEventPublisher;
    @Mock
    private RuleEvaluationService evaluationService;

    @InjectMocks
    private EvaluateMetricHandler handler;

    private Rule buildRule(UUID sourceId, AlertSeverity severity) {
        return new Rule(UUID.randomUUID(), "cpu-alert", sourceId, "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, severity, true);
    }

    @Test
    void evaluate_shouldPublishAlertWhenRuleTriggered() {
        UUID sourceId = UUID.randomUUID();
        Rule rule = buildRule(sourceId, AlertSeverity.WARNING);

        when(ruleRepository.findActiveBySourceAndMetric(sourceId, "cpu_usage")).thenReturn(List.of(rule));
        when(evaluationService.evaluate(eq(rule), eq(95.0))).thenReturn(true);
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.evaluate(sourceId, "cpu_usage", 95.0);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertEventPublisher).publish(captor.capture());
        Alert alert = captor.getValue();
        assertThat(alert.ruleId()).isEqualTo(rule.id());
        assertThat(alert.sourceId()).isEqualTo(sourceId);
        assertThat(alert.value()).isEqualTo(95.0);
        assertThat(alert.severity()).isEqualTo(AlertSeverity.WARNING);
        assertThat(alert.message()).contains("cpu-alert");
    }

    @Test
    void evaluate_shouldNotPublishWhenRuleNotTriggered() {
        UUID sourceId = UUID.randomUUID();
        Rule rule = buildRule(sourceId, AlertSeverity.WARNING);

        when(ruleRepository.findActiveBySourceAndMetric(sourceId, "cpu_usage")).thenReturn(List.of(rule));
        when(evaluationService.evaluate(eq(rule), eq(50.0))).thenReturn(false);

        handler.evaluate(sourceId, "cpu_usage", 50.0);

        verify(alertEventPublisher, never()).publish(any());
        verify(alertRepository, never()).save(any());
    }

    @Test
    void evaluate_shouldDoNothingWhenNoActiveRules() {
        UUID sourceId = UUID.randomUUID();
        when(ruleRepository.findActiveBySourceAndMetric(sourceId, "cpu_usage")).thenReturn(List.of());

        handler.evaluate(sourceId, "cpu_usage", 95.0);

        verify(alertEventPublisher, never()).publish(any());
    }

    @Test
    void evaluate_shouldPublishOneAlertPerTriggeredRule() {
        UUID sourceId = UUID.randomUUID();
        Rule rule1 = buildRule(sourceId, AlertSeverity.WARNING);
        Rule rule2 = buildRule(sourceId, AlertSeverity.CRITICAL);

        when(ruleRepository.findActiveBySourceAndMetric(sourceId, "cpu_usage")).thenReturn(List.of(rule1, rule2));
        when(evaluationService.evaluate(eq(rule1), eq(90.0))).thenReturn(true);
        when(evaluationService.evaluate(eq(rule2), eq(90.0))).thenReturn(true);
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.evaluate(sourceId, "cpu_usage", 90.0);

        verify(alertEventPublisher, times(2)).publish(any());
    }

    @Test
    void evaluate_shouldOnlyPublishForTriggeredRulesInMixedList() {
        UUID sourceId = UUID.randomUUID();
        Rule triggered = buildRule(sourceId, AlertSeverity.CRITICAL);
        Rule notTriggered = buildRule(sourceId, AlertSeverity.INFO);

        when(ruleRepository.findActiveBySourceAndMetric(sourceId, "cpu_usage")).thenReturn(List.of(triggered, notTriggered));
        when(evaluationService.evaluate(eq(triggered), eq(70.0))).thenReturn(true);
        when(evaluationService.evaluate(eq(notTriggered), eq(70.0))).thenReturn(false);
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.evaluate(sourceId, "cpu_usage", 70.0);

        verify(alertEventPublisher, times(1)).publish(any());
    }
}
