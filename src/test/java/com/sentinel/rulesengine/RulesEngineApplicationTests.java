package com.sentinel.rulesengine;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Test d'intégration — nécessite PostgreSQL et Redis (lancer docker-compose avant)")
class RulesEngineApplicationTests {

	@Test
	void contextLoads() {
	}

}
