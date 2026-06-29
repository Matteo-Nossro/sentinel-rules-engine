package com.sentinel.rulesengine.infrastructure.persistence.entity;

import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.RuleType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rules", schema = "rules_engine")
public class RuleEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType type;

    private Double threshold;

    @Column(name = "window_size")
    private Long windowSize;

    private Integer frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private boolean active;

    public RuleEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }

    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }

    public RuleType getType() { return type; }
    public void setType(RuleType type) { this.type = type; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public Long getWindowSize() { return windowSize; }
    public void setWindowSize(Long windowSize) { this.windowSize = windowSize; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
