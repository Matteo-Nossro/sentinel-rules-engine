package com.sentinel.rulesengine.infrastructure.persistence;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.domain.port.out.RuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RuleRepositoryAdapter implements RuleRepository {

    private final RuleJpaRepository jpaRepository;
    private final RuleMapper mapper;

    public RuleRepositoryAdapter(RuleJpaRepository jpaRepository, RuleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Rule save(Rule rule) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(rule)));
    }

    @Override
    public Optional<Rule> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Rule> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Rule> findActiveBySourceAndMetric(UUID sourceId, String metricName) {
        return mapper.toDomainList(
                jpaRepository.findByActiveTrueAndSourceIdAndMetricName(sourceId, metricName)
        );
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
