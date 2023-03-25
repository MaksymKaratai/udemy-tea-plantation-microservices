package com.tea.inventory;

import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@Import(value = {CommonControllerExceptionHandler.class, AmqpConfig.class})
public class TeaInventoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(TeaInventoryApplication.class, args);
	}
}
