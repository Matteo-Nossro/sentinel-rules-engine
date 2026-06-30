package com.sentinel.rulesengine.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.model.RuleType;
import com.sentinel.rulesengine.domain.port.in.CreateRuleUseCase;
import com.sentinel.rulesengine.domain.port.in.DeleteRuleUseCase;
import com.sentinel.rulesengine.domain.port.in.GetRulesQuery;
import com.sentinel.rulesengine.domain.port.in.UpdateRuleUseCase;
import com.sentinel.rulesengine.infrastructure.config.SecurityConfig;
import com.sentinel.rulesengine.infrastructure.web.dto.RuleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
@Import(SecurityConfig.class)
class RuleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateRuleUseCase createRuleUseCase;

    @MockitoBean
    UpdateRuleUseCase updateRuleUseCase;

    @MockitoBean
    DeleteRuleUseCase deleteRuleUseCase;

    @MockitoBean
    GetRulesQuery getRulesQuery;

    private Rule sampleRule() {
        return new Rule(UUID.randomUUID(), "cpu-alert", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);
    }

    private RuleRequest validRequest() {
        return new RuleRequest("cpu-alert", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);
    }

    @Test
    void getAll_shouldReturn200WithRuleList() throws Exception {
        when(getRulesQuery.getAll()).thenReturn(List.of(sampleRule()));

        mockMvc.perform(get("/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("cpu-alert"))
                .andExpect(jsonPath("$[0].type").value("THRESHOLD"));
    }

    @Test
    void create_shouldReturn201WithCreatedRule() throws Exception {
        Rule created = sampleRule();
        when(createRuleUseCase.create(any())).thenReturn(created);

        mockMvc.perform(post("/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("cpu-alert"));
    }

    @Test
    void create_shouldReturn400WhenNameIsBlank() throws Exception {
        RuleRequest invalid = new RuleRequest("", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);

        mockMvc.perform(post("/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenSeverityIsNull() throws Exception {
        RuleRequest invalid = new RuleRequest("cpu-alert", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 80.0, null, null, null, true);

        mockMvc.perform(post("/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenMetricNameIsBlank() throws Exception {
        RuleRequest invalid = new RuleRequest("cpu-alert", UUID.randomUUID(), "",
                RuleType.THRESHOLD, 80.0, null, null, AlertSeverity.WARNING, true);

        mockMvc.perform(post("/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200WithUpdatedRule() throws Exception {
        UUID id = UUID.randomUUID();
        Rule updated = new Rule(id, "cpu-alert-critical", UUID.randomUUID(), "cpu_usage",
                RuleType.THRESHOLD, 90.0, null, null, AlertSeverity.CRITICAL, true);
        when(updateRuleUseCase.update(any(), any())).thenReturn(updated);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/rules/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("cpu-alert-critical"))
                .andExpect(jsonPath("$.severity").value("CRITICAL"));
    }

    @Test
    void delete_shouldReturn204AndCallUseCase() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/rules/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteRuleUseCase).delete(id);
    }
}
