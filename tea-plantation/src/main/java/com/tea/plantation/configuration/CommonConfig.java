package com.tea.plantation.configuration;

import com.tea.common.exception.CommonControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {CommonControllerExceptionHandler.class})
public class CommonConfig {
}
