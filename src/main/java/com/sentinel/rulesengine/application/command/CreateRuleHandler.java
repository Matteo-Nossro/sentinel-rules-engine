package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.in.CreateRuleUseCase;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateRuleHandler implements CreateRuleUseCase {

    private final RuleRepository ruleRepository;

    public CreateRuleHandler(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public Rule create(Rule rule) {
        Rule toSave = new Rule(
                UUID.randomUUID(),
                rule.name(),
                rule.sourceId(),
                rule.metricName(),
                rule.type(),
                rule.threshold(),
                rule.windowSize(),
                rule.frequency(),
                rule.severity(),
                rule.active()
        );
        return ruleRepository.save(toSave);
    }
}
