package com.tea.order;

import com.tea.common.client.PoolingApacheHttpClientRestTemplateCustomizer;
import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpCommonBeansConfig;
import com.tea.common.messaging.AmqpOrderProcessingConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
		info = @Info(title = "Order service API", version = "0.0.1", description = "Service for making tea orders"),
		servers = @Server(url = "/", description = "Gateway")
)
@EnableJpaAuditing
@SpringBootApplication
@Import(value = {
	AmqpCommonBeansConfig.class,
	AmqpOrderProcessingConfig.class,
	CommonControllerExceptionHandler.class,
	PoolingApacheHttpClientRestTemplateCustomizer.class
})
public class TeaOrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(TeaOrderApplication.class, args);
	}
}
