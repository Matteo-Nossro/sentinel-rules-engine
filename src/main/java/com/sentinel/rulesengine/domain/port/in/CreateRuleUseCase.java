package com.sentinel.rulesengine.domain.port.in;

import com.sentinel.rulesengine.domain.model.Rule;

public interface CreateRuleUseCase {
    Rule create(Rule rule);
}
