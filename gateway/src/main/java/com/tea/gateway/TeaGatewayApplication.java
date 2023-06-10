package com.tea.gateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
        info = @Info(title = "Plantation system", version = "0.0.1"),
        servers = @Server(url = "/", description = "Gateway")
)
@SpringBootApplication
public class TeaGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeaGatewayApplication.class, args);
    }
}
