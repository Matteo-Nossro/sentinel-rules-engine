package com.sentinel.rulesengine.domain.model;

public enum ComparisonOperator {
    GT(">"), GTE(">="), LT("<"), LTE("<=");

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static ComparisonOperator fromSymbol(String symbol) {
        for (ComparisonOperator operator : values()) {
            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Opérateur inconnu : " + symbol);
    }
}
