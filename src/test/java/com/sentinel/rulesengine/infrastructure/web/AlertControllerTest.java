package com.sentinel.rulesengine.infrastructure.web;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.model.AlertSeverity;
import com.sentinel.rulesengine.domain.port.in.AcknowledgeAlertUseCase;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import com.sentinel.rulesengine.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@Import(SecurityConfig.class)
class AlertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AlertRepository alertRepository;

    @MockitoBean
    AcknowledgeAlertUseCase acknowledgeAlertUseCase;

    private Alert sampleAlert(boolean acknowledged) {
        return new Alert(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 94.5,
                AlertSeverity.CRITICAL, Instant.now(), "cpu_usage = 94.5", acknowledged);
    }

    @Test
    void getAll_shouldReturn200WithAlertList() throws Exception {
        when(alertRepository.findAll()).thenReturn(List.of(sampleAlert(false)));

        mockMvc.perform(get("/alerts").header("X-User-Id", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].acknowledged").value(false));
    }

    @Test
    void getAll_shouldReturn401WhenUserIdHeaderMissing() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acknowledge_shouldReturn200WithAcknowledgedAlert() throws Exception {
        UUID id = UUID.randomUUID();
        Alert acknowledged = sampleAlert(true);
        when(acknowledgeAlertUseCase.acknowledge(id)).thenReturn(acknowledged);

        mockMvc.perform(patch("/alerts/{id}", id).header("X-User-Id", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acknowledged").value(true));
    }

    @Test
    void acknowledge_shouldReturn404WhenAlertDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(acknowledgeAlertUseCase.acknowledge(id)).thenThrow(new NoSuchElementException("Alert not found: " + id));

        mockMvc.perform(patch("/alerts/{id}", id).header("X-User-Id", "test-user"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Alert not found: " + id));
    }
}
