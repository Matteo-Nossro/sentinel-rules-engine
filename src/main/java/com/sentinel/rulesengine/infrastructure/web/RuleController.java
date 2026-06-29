package com.sentinel.rulesengine.infrastructure.web;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.in.CreateRuleUseCase;
import com.sentinel.rulesengine.domain.port.in.DeleteRuleUseCase;
import com.sentinel.rulesengine.domain.port.in.GetRulesQuery;
import com.sentinel.rulesengine.domain.port.in.UpdateRuleUseCase;
import com.sentinel.rulesengine.infrastructure.web.dto.RuleRequest;
import com.sentinel.rulesengine.infrastructure.web.dto.RuleResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final CreateRuleUseCase createRuleUseCase;
    private final UpdateRuleUseCase updateRuleUseCase;
    private final DeleteRuleUseCase deleteRuleUseCase;
    private final GetRulesQuery getRulesQuery;

    public RuleController(CreateRuleUseCase createRuleUseCase,
                          UpdateRuleUseCase updateRuleUseCase,
                          DeleteRuleUseCase deleteRuleUseCase,
                          GetRulesQuery getRulesQuery) {
        this.createRuleUseCase = createRuleUseCase;
        this.updateRuleUseCase = updateRuleUseCase;
        this.deleteRuleUseCase = deleteRuleUseCase;
        this.getRulesQuery = getRulesQuery;
    }

    @GetMapping
    public List<RuleResponse> getAll() {
        return getRulesQuery.getAll().stream().map(RuleResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<RuleResponse> create(@RequestBody @Valid RuleRequest request) {
        Rule rule = fromRequest(null, request);
        return ResponseEntity.status(201).body(RuleResponse.from(createRuleUseCase.create(rule)));
    }

    @PutMapping("/{id}")
    public RuleResponse update(@PathVariable UUID id, @RequestBody @Valid RuleRequest request) {
        return RuleResponse.from(updateRuleUseCase.update(id, fromRequest(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteRuleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Rule fromRequest(UUID id, RuleRequest request) {
        Duration windowSize = request.windowSizeSeconds() != null
                ? Duration.ofSeconds(request.windowSizeSeconds())
                : null;
        return new Rule(id, request.name(), request.sourceId(), request.metricName(),
                request.type(), request.threshold(), windowSize,
                request.frequency(), request.severity(), request.active());
    }
}
