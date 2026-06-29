package com.sentinel.rulesengine.infrastructure.config;

import com.sentinel.rulesengine.domain.service.RuleEvaluationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    RuleEvaluationService ruleEvaluationService() {
        return new RuleEvaluationService();
    }
}
