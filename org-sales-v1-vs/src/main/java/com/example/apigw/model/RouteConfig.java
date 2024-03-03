package com.example.apigw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class RouteConfig {
    private String id;
    private String path;
    private String method;
    private boolean isReadBody;
    private Class<Sale> classBody; // to review
    private List<GatewayFilter> filters;
    private String backEndUri;
}
