package com.sentinel.rulesengine.application.command;

import com.sentinel.rulesengine.domain.port.in.DeleteRuleUseCase;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteRuleHandler implements DeleteRuleUseCase {

    private final RuleRepository ruleRepository;

    public DeleteRuleHandler(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public void delete(UUID id) {
        ruleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rule not found: " + id));
        ruleRepository.deleteById(id);
    }
}
