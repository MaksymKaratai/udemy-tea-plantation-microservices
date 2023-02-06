package com.tea.plantation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@Profile("integration")
@SpringBootTest
class PlantationApplicationTests {
	@Test
	void contextLoads() {
	}
}
