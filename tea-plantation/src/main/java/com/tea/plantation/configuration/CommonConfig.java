package com.tea.plantation.configuration;

import com.tea.common.client.PoolingApacheHttpClientRestTemplateCustomizer;
import com.tea.common.exception.CommonControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {CommonControllerExceptionHandler.class, PoolingApacheHttpClientRestTemplateCustomizer.class})
public class CommonConfig {}
