package com.tea.inventory;

import com.tea.common.exception.CommonControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@Import(value = {CommonControllerExceptionHandler.class})
public class TeaInventoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(TeaInventoryApplication.class, args);
	}
}
