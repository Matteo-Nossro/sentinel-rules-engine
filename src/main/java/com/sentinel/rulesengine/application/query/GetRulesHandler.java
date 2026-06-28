package com.sentinel.rulesengine.application.query;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.in.GetRulesQuery;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRulesHandler implements GetRulesQuery {

    private final RuleRepository ruleRepository;

    public GetRulesHandler(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public List<Rule> getAll() {
        return ruleRepository.findAll();
    }
}
