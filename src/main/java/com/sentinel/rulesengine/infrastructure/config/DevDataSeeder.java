package com.sentinel.rulesengine.infrastructure.config;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.ComparisonOperator;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// profil "seed" uniquement — UUID fixes, partages avec le seeder d'ingestion-service, ne pas changer isolement
@Component
@Profile("seed")
public class DevDataSeeder implements ApplicationRunner {

    private static final UUID SOURCE_WEB_01 = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    private static final UUID SOURCE_DB_PRIMARY = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
    private static final UUID SOURCE_API_GATEWAY = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");
    private static final UUID SOURCE_WORKER_BATCH = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000004");

    private static final UUID RULE_CPU_WEB = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001");
    private static final UUID RULE_MEMORY_WEB = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
    private static final UUID RULE_LATENCY_GATEWAY = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000003");
    private static final UUID RULE_DISK_DB = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000004");
    private static final UUID RULE_CPU_WORKER = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000005");

    private final RuleRepository ruleRepository;
    private final AlertRepository alertRepository;

    public DevDataSeeder(RuleRepository ruleRepository, AlertRepository alertRepository) {
        this.ruleRepository = ruleRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // idempotent par ID fixe, pas juste "table vide" — evite de re-semer par-dessus des regles manuelles
        if (ruleRepository.findById(RULE_CPU_WEB).isPresent()) {
            return;
        }

        List.of(
                new Rule(RULE_CPU_WEB, "CPU critique - prod-web-01", SOURCE_WEB_01, "cpu_usage",
                        RuleType.THRESHOLD, ComparisonOperator.GT, 90.0, null, null, AlertSeverity.CRITICAL, true),
                new Rule(RULE_MEMORY_WEB, "Mémoire élevée - prod-web-01", SOURCE_WEB_01, "memory_rss",
                        RuleType.THRESHOLD, ComparisonOperator.GT, 80.0, null, null, AlertSeverity.WARNING, true),
                new Rule(RULE_LATENCY_GATEWAY, "Latence HTTP - prod-api-gateway", SOURCE_API_GATEWAY, "http_response_time",
                        RuleType.THRESHOLD, ComparisonOperator.GTE, 500.0, null, null, AlertSeverity.WARNING, true),
                new Rule(RULE_DISK_DB, "Disque plein - prod-db-01", SOURCE_DB_PRIMARY, "disk_usage",
                        RuleType.THRESHOLD, ComparisonOperator.GT, 95.0, null, null, AlertSeverity.CRITICAL, true),
                new Rule(RULE_CPU_WORKER, "CPU élevé - worker-batch-01", SOURCE_WORKER_BATCH, "cpu_usage",
                        RuleType.THRESHOLD, ComparisonOperator.GT, 85.0, null, null, AlertSeverity.INFO, true)
        ).forEach(ruleRepository::save);

        // pas d'ID fixe ici, la garde sur RULE_CPU_WEB plus haut suffit
        Instant now = Instant.now();

        List.of(
                alert(RULE_CPU_WEB, SOURCE_WEB_01, 94.5, AlertSeverity.CRITICAL, now.minus(Duration.ofMinutes(20)),
                        "cpu_usage = 94.5 sur prod-web-01 (seuil : 90.0)"),
                alert(RULE_MEMORY_WEB, SOURCE_WEB_01, 83.2, AlertSeverity.WARNING, now.minus(Duration.ofHours(1).plusMinutes(10)),
                        "memory_rss = 83.2 sur prod-web-01 (seuil : 80.0)"),
                alert(RULE_LATENCY_GATEWAY, SOURCE_API_GATEWAY, 612.0, AlertSeverity.WARNING, now.minus(Duration.ofHours(2).plusMinutes(45)),
                        "http_response_time = 612.0 sur prod-api-gateway (seuil : 500.0)"),
                alert(RULE_DISK_DB, SOURCE_DB_PRIMARY, 97.8, AlertSeverity.CRITICAL, now.minus(Duration.ofHours(3).plusMinutes(30)),
                        "disk_usage = 97.8 sur prod-db-01 (seuil : 95.0)"),
                alert(RULE_CPU_WORKER, SOURCE_WORKER_BATCH, 88.1, AlertSeverity.INFO, now.minus(Duration.ofHours(5)),
                        "cpu_usage = 88.1 sur worker-batch-01 (seuil : 85.0)"),
                alert(RULE_CPU_WEB, SOURCE_WEB_01, 91.0, AlertSeverity.CRITICAL, now.minus(Duration.ofHours(7).plusMinutes(15)),
                        "cpu_usage = 91.0 sur prod-web-01 (seuil : 90.0)"),
                alert(RULE_MEMORY_WEB, SOURCE_WEB_01, 81.4, AlertSeverity.WARNING, now.minus(Duration.ofHours(10)),
                        "memory_rss = 81.4 sur prod-web-01 (seuil : 80.0)"),
                acknowledgedAlert(RULE_LATENCY_GATEWAY, SOURCE_API_GATEWAY, 540.0, AlertSeverity.WARNING, now.minus(Duration.ofHours(14).plusMinutes(20)),
                        "http_response_time = 540.0 sur prod-api-gateway (seuil : 500.0)"),
                acknowledgedAlert(RULE_DISK_DB, SOURCE_DB_PRIMARY, 96.3, AlertSeverity.CRITICAL, now.minus(Duration.ofHours(18)),
                        "disk_usage = 96.3 sur prod-db-01 (seuil : 95.0)"),
                acknowledgedAlert(RULE_CPU_WORKER, SOURCE_WORKER_BATCH, 86.7, AlertSeverity.INFO, now.minus(Duration.ofHours(22).plusMinutes(30)),
                        "cpu_usage = 86.7 sur worker-batch-01 (seuil : 85.0)")
        ).forEach(alertRepository::save);
    }

    private static Alert alert(UUID ruleId, UUID sourceId, double value, AlertSeverity severity, Instant triggeredAt, String message) {
        return new Alert(UUID.randomUUID(), ruleId, sourceId, value, severity, triggeredAt, message, false);
    }

    // Les plus anciennes sont deja acquittees, pour que la demo montre les deux etats.
    private static Alert acknowledgedAlert(UUID ruleId, UUID sourceId, double value, AlertSeverity severity, Instant triggeredAt, String message) {
        return new Alert(UUID.randomUUID(), ruleId, sourceId, value, severity, triggeredAt, message, true);
    }
}
