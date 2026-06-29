package com.sentinel.rulesengine.infrastructure.persistence;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.infrastructure.persistence.entity.AlertEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    Alert toDomain(AlertEntity entity);
    AlertEntity toEntity(Alert alert);
    List<Alert> toDomainList(List<AlertEntity> entities);
}
