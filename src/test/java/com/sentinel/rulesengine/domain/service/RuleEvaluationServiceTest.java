package com.sentinel.rulesengine.domain.service;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleEvaluationServiceTest {

    private RuleEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new RuleEvaluationService();
    }

    private Rule thresholdRule(double threshold) {
        return new Rule(UUID.randomUUID(), "cpu-alert", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, threshold, null, null, AlertSeverity.WARNING, true);
    }

    @Test
    void evaluate_shouldReturnTrueWhenValueExceedsThreshold() {
        assertThat(service.evaluate(thresholdRule(80.0), 85.0)).isTrue();
    }

    @Test
    void evaluate_shouldReturnFalseWhenValueBelowThreshold() {
        assertThat(service.evaluate(thresholdRule(80.0), 75.0)).isFalse();
    }

    @Test
    void evaluate_shouldReturnFalseWhenValueEqualsThreshold() {
        // strictement supérieur à, pas >=
        assertThat(service.evaluate(thresholdRule(80.0), 80.0)).isFalse();
    }

    @Test
    void evaluate_shouldThrowForSlidingWindow() {
        Rule rule = new Rule(UUID.randomUUID(), "r", UUID.randomUUID(), "cpu",
                RuleType.SLIDING_WINDOW, null, null, null, AlertSeverity.INFO, true);
        assertThatThrownBy(() -> service.evaluate(rule, 50.0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void evaluate_shouldThrowForFrequency() {
        Rule rule = new Rule(UUID.randomUUID(), "r", UUID.randomUUID(), "cpu",
                RuleType.FREQUENCY, null, null, 5, AlertSeverity.INFO, true);
        assertThatThrownBy(() -> service.evaluate(rule, 50.0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void evaluate_shouldThrowForAbsence() {
        Rule rule = new Rule(UUID.randomUUID(), "r", UUID.randomUUID(), "cpu",
                RuleType.ABSENCE, null, null, null, AlertSeverity.CRITICAL, true);
        assertThatThrownBy(() -> service.evaluate(rule, 0.0))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
