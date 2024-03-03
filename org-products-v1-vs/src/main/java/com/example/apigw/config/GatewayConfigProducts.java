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
public class GatewayConfigProducts {
    private static final String VIRTUALIZATION_NAME = "org-products-v1-vs";
    private static final String API_BASE_URL = "http://localhost:8083";

    @Bean(VIRTUALIZATION_NAME)
    public List<RouteConfig> virtualizationConfigs() {
        return Arrays.asList(
                createRouteConfig(
                        "/products",
                        "POST",
                        new LogConfig(ENABLED,ENABLED),
                        new AuthConfig(AuthMode.DISABLED,Arrays.asList("/login"))),
                createRouteConfig(
                        "/products",
                        "GET",
                        new LogConfig(DISABLED,ENABLED),
                        new AuthConfig(AuthMode.DISABLED,Arrays.asList("/login")))
        );
    }

    private RouteConfig createRouteConfig(String path, String method, LogConfig logConfig, AuthConfig authConfig) {
        return new RouteConfig(
                VIRTUALIZATION_NAME,
                path,
                method,
                logConfig.getRequestLogger()==ENABLED,
                Product.class,
                Arrays.asList(
                        LogFilterFactory.create(logConfig)
                        ,AuthFilterFactory.create(authConfig)),
                API_BASE_URL
        );
    }
}
