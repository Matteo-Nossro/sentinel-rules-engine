package com.sentinel.rulesengine.infrastructure.persistence.entity;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts", schema = "rules_engine")
public class AlertEntity {

    @Id
    private UUID id;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(nullable = false)
    private double value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;

    @Column(nullable = false, length = 1000)
    private String message;

    public AlertEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public Instant getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Instant triggeredAt) { this.triggeredAt = triggeredAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
