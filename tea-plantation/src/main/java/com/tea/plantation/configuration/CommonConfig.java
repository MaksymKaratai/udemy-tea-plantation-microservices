package com.tea.plantation.configuration;

import com.tea.common.client.PoolingApacheHttpClientRestTemplateCustomizer;
import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpConfig;
import com.tea.common.messaging.AmqpOrderProcessingConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Import(value = {
        AmqpConfig.class,
        AmqpOrderProcessingConfig.class,
        CommonControllerExceptionHandler.class,
        PoolingApacheHttpClientRestTemplateCustomizer.class
})
public class CommonConfig {
}
