package com.sentinel.rulesengine.domain.port.in;

import com.sentinel.rulesengine.domain.model.Rule;

import java.util.UUID;

public interface UpdateRuleUseCase {
    Rule update(UUID id, Rule rule);
}
