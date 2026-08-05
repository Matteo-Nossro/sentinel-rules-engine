package com.sentinel.rulesengine.domain.service;

import com.sentinel.rulesengine.domain.model.ComparisonOperator;
import com.sentinel.rulesengine.domain.model.Rule;

public class RuleEvaluationService {

    public boolean evaluate(Rule rule, double value) {
        return switch (rule.type()) {
            case THRESHOLD -> compare(value, rule.operator(), rule.threshold());
            // nécessite un état temporel Redis — pas géré ici
            case SLIDING_WINDOW, FREQUENCY, ABSENCE ->
                throw new UnsupportedOperationException("Stateful evaluation not supported: " + rule.type());
        };
    }

    // Opérateur absent (règles créées avant son ajout) : ">" strict, comportement historique.
    private boolean compare(double value, ComparisonOperator operator, double threshold) {
        ComparisonOperator effective = operator != null ? operator : ComparisonOperator.GT;
        return switch (effective) {
            case GT -> value > threshold;
            case GTE -> value >= threshold;
            case LT -> value < threshold;
            case LTE -> value <= threshold;
        };
    }
}
