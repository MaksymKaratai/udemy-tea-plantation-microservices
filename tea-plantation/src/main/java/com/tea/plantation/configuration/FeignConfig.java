package com.tea.plantation.configuration;

import com.tea.plantation.services.client.InventoryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local")
@Configuration
@EnableFeignClients(clients = {InventoryClient.class})
public class FeignConfig {
}
