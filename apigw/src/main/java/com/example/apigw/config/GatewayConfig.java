package com.example.apigw.config;


import com.example.apigw.model.RouteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;

@Configuration
public class GatewayConfig {

    @Autowired
    List<List<RouteConfig>> routeConfigs;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes =  builder.routes();
        routeConfigs.stream()
                .flatMap(Collection::stream)
                .forEach(routeConfig -> addRoute(routes,routeConfig));

        return routes.build();
    }

    private void addRoute(RouteLocatorBuilder.Builder routes, RouteConfig routeConfig){
        if(routeConfig.isReadBody()){
            routes.route(routeConfig.getId(),
                    r->r.path("/"+routeConfig.getId()+routeConfig.getPath()+"/**")
                    .and().method(routeConfig.getMethod())
                    .and().readBody(routeConfig.getClassBody(),s->routeConfig.isReadBody())
                    .filters(f-> f.filters(routeConfig.getFilters()))
                    .uri(routeConfig.getBackEndUri()));
        }else{
            routes.route(routeConfig.getId(),
                    r->r.path("/"+routeConfig.getId()+routeConfig.getPath()+"/**")
                    .and().method(routeConfig.getMethod())
                    .filters(f-> f.filters(routeConfig.getFilters()))
                    .uri(routeConfig.getBackEndUri()));
        }
    }
}
