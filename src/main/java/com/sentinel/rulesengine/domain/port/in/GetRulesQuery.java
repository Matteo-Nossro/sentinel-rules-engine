package com.sentinel.rulesengine.domain.port.in;

import com.sentinel.rulesengine.domain.model.Rule;

import java.util.List;

public interface GetRulesQuery {
    List<Rule> getAll();
}
