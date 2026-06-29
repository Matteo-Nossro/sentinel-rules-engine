package com.sentinel.rulesengine.domain.port.in;

import java.util.UUID;

public interface DeleteRuleUseCase {
    void delete(UUID id);
}
