package com.example.apigw.config;

import com.example.apigw.filter.AuthFilterFactory;
import com.example.apigw.filter.LogFilterFactory;
import com.example.apigw.model.*;
import com.example.apigw.model.AuthConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static com.example.apigw.model.LogLevel.DISABLED;
import static com.example.apigw.model.LogLevel.ENABLED;

@Configuration
public class GatewayConfigSales { // config class name
    private final String VIRTUALIZATION_NAME = "org-sales-v1-vs";
    private static final String API_BASE_URL = "http://localhost:8082";

    @Bean(VIRTUALIZATION_NAME)
    public List<RouteConfig> virtualizationConfigs(){
        return Arrays.asList(
                createRouteConfig("/sales", "POST", new LogConfig(ENABLED,ENABLED), new AuthConfig(AuthMode.ENABLED,Arrays.asList("/login"))),
                createRouteConfig("/sales", "GET", new LogConfig(DISABLED,ENABLED), new AuthConfig(AuthMode.ENABLED,Arrays.asList("/sales")))
        );
    }
    private RouteConfig createRouteConfig(String path, String method, LogConfig logConfig, AuthConfig authConfig) {
        return new RouteConfig(
                VIRTUALIZATION_NAME,
                path,
                method,
                logConfig.getRequestLogger()==ENABLED,
                Sale.class,
                Arrays.asList(
                        LogFilterFactory.create(logConfig)
                        ,AuthFilterFactory.create(authConfig)),
                API_BASE_URL
        );
    }
}
