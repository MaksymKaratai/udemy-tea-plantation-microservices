package com.tea.plantation.configuration;

import com.tea.plantation.services.client.InventoryClient;
import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local")
@Configuration
@EnableFeignClients(clients = {InventoryClient.class})
public class FeignConfig {
    @Bean
    public Capability micrometerCapability(final MeterRegistry registry) {
        return new MicrometerCapability(registry);
    }
}
