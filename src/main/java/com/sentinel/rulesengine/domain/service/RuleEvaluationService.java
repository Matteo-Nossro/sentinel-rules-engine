package com.sentinel.rulesengine.domain.service;

import com.sentinel.rulesengine.domain.model.Rule;

public class RuleEvaluationService {

    public boolean evaluate(Rule rule, double value) {
        return switch (rule.type()) {
            case THRESHOLD -> value > rule.threshold();
            // nécessite un état temporel Redis — pas géré ici
            case SLIDING_WINDOW, FREQUENCY, ABSENCE ->
                throw new UnsupportedOperationException("Stateful evaluation not supported: " + rule.type());
        };
    }
}
