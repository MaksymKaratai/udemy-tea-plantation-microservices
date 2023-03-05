package com.tea.order;

import com.tea.common.exception.CommonControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {CommonControllerExceptionHandler.class})
public class TeaOrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(TeaOrderApplication.class, args);
	}
}
