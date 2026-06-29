package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.in.UpdateRuleUseCase;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateRuleHandler implements UpdateRuleUseCase {

    private final RuleRepository ruleRepository;

    public UpdateRuleHandler(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public Rule update(UUID id, Rule rule) {
        ruleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rule not found: " + id));

        Rule toSave = new Rule(id, rule.name(), rule.sourceId(), rule.metricName(),
                rule.type(), rule.threshold(), rule.windowSize(),
                rule.frequency(), rule.severity(), rule.active());

        return ruleRepository.save(toSave);
    }
}
