package com.tea.order.configuration;

import com.tea.order.services.client.PlantationClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local")
@Configuration
@EnableFeignClients(clients = {PlantationClient.class})
public class FeignConfig {}
