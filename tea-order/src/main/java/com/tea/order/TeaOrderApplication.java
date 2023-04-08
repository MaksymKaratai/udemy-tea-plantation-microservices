package com.tea.order;

import com.tea.common.client.PoolingApacheHttpClientRestTemplateCustomizer;
import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpCommonBeansConfig;
import com.tea.common.messaging.AmqpOrderProcessingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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
