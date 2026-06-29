package com.sentinel.rulesengine.infrastructure.persistence;

import com.sentinel.rulesengine.domain.model.Rule;
import com.sentinel.rulesengine.infrastructure.persistence.entity.RuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RuleMapper {

    @Mapping(target = "windowSize", qualifiedByName = "toDuration")
    Rule toDomain(RuleEntity entity);

    @Mapping(target = "windowSize", qualifiedByName = "toSeconds")
    RuleEntity toEntity(Rule rule);

    List<Rule> toDomainList(List<RuleEntity> entities);

    @Named("toDuration")
    default Duration toDuration(Long seconds) {
        return seconds != null ? Duration.ofSeconds(seconds) : null;
    }

    @Named("toSeconds")
    default Long toSeconds(Duration duration) {
        return duration != null ? duration.getSeconds() : null;
    }
}
