package com.sentinel.rulesengine.infrastructure.web.dto;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.RuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record RuleRequest(
        @NotBlank String name,
        @NotNull UUID sourceId,
        @NotBlank String metricName,
        @NotNull RuleType type,
        // symbole brut, converti en ComparisonOperator dans le controller — optionnel, seul THRESHOLD l'utilise
        @Pattern(regexp = "^(>|>=|<|<=)$", message = "doit être l'un de : >, >=, <, <=") String operator,
        Double threshold,
        Long windowSizeSeconds,
        Integer frequency,
        @NotNull AlertSeverity severity,
        boolean active
) {}
