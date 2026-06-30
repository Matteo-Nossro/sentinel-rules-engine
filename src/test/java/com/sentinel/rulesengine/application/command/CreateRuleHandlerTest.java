package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRuleHandlerTest {

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private CreateRuleHandler handler;

    private Rule inputRule() {
        return new Rule(null, "cpu-alert", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);
    }

    @Test
    void create_shouldGenerateNewIdBeforeSave() {
        Rule input = inputRule();
        when(ruleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.create(input);

        ArgumentCaptor<Rule> captor = ArgumentCaptor.forClass(Rule.class);
        verify(ruleRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isNotNull();
    }

    @Test
    void create_shouldPreserveAllFieldsExceptId() {
        UUID sourceId = UUID.randomUUID();
        Rule input = new Rule(null, "mem-alert", sourceId, "memory_rss",
                RuleType.THRESHOLD, 90.0, null, null, AlertSeverity.CRITICAL, false);
        when(ruleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.create(input);

        ArgumentCaptor<Rule> captor = ArgumentCaptor.forClass(Rule.class);
        verify(ruleRepository).save(captor.capture());
        Rule saved = captor.getValue();
        assertThat(saved.name()).isEqualTo("mem-alert");
        assertThat(saved.sourceId()).isEqualTo(sourceId);
        assertThat(saved.metricName()).isEqualTo("memory_rss");
        assertThat(saved.threshold()).isEqualTo(90.0);
        assertThat(saved.severity()).isEqualTo(AlertSeverity.CRITICAL);
        assertThat(saved.active()).isFalse();
    }

    @Test
    void create_shouldReturnSavedRule() {
        Rule input = inputRule();
        Rule persisted = new Rule(UUID.randomUUID(), input.name(), input.sourceId(), input.metricName(),
                input.type(), input.threshold(), null, null, input.severity(), input.active());
        when(ruleRepository.save(any())).thenReturn(persisted);

        Rule result = handler.create(input);

        assertThat(result).isEqualTo(persisted);
    }
}
