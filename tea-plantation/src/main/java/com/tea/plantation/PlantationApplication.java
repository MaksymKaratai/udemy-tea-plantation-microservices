package com.tea.plantation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@OpenAPIDefinition(
	info = @Info(title = "Plantation service API", version = "0.0.1", description = "Service that manage tea entities"),
	servers = @Server(url = "/", description = "Gateway")
)
@EnableMongoAuditing
@SpringBootApplication
public class PlantationApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlantationApplication.class, args);
	}
}
