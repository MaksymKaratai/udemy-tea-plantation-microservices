package com.tea.inventory;

import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		info = @Info(title = "Inventory service API", version = "0.0.1", description = "Service for warehouse management"),
		servers = @Server(url = "/", description = "Gateway")
)
@EnableJpaAuditing
@SpringBootApplication
@Import(value = {CommonControllerExceptionHandler.class, AmqpConfig.class})
public class TeaInventoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(TeaInventoryApplication.class, args);
	}
}
