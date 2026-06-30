package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.ArgumentCaptor;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRuleHandlerTest {

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private UpdateRuleHandler handler;

    private Rule buildRule(UUID id, String name) {
        return new Rule(id, name, UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);
    }

    @Test
    void update_shouldThrowWhenRuleNotFound() {
        UUID id = UUID.randomUUID();
        when(ruleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.update(id, buildRule(id, "cpu-alert")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_shouldSaveRuleWithOriginalId() {
        UUID id = UUID.randomUUID();
        Rule existing = buildRule(id, "old-name");
        Rule updated = buildRule(id, "new-name");

        when(ruleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ruleRepository.save(any())).thenReturn(updated);

        Rule result = handler.update(id, updated);

        verify(ruleRepository).save(any());
        assertThat(result.name()).isEqualTo("new-name");
    }

    @Test
    void update_shouldForcePathIdEvenIfBodyCarriesDifferentOne() {
        UUID id = UUID.randomUUID();
        Rule existing = buildRule(id, "cpu-alert");
        Rule updatePayload = buildRule(UUID.randomUUID(), "cpu-alert-updated"); // id différent dans le body

        when(ruleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ruleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.update(id, updatePayload);

        ArgumentCaptor<Rule> captor = ArgumentCaptor.forClass(Rule.class);
        verify(ruleRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().name()).isEqualTo("cpu-alert-updated");
    }
}
