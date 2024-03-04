package com.example.apigw.filter;
 
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
 
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate()
                .request(rewriteRequest(exchange.getRequest()))
                .build());
    }
 
    private ServerHttpRequest rewriteRequest(ServerHttpRequest originalRequest) {
        // Perform URL rewriting logic here
        String originalPath = originalRequest.getURI().getPath();
        String newPath = originalPath.replaceFirst("/[^/]+", "");
 
        // Create a new request with the modified path
        return originalRequest.mutate()
                .path(newPath)
                .build();
    }
 
    @Override
    public int getOrder() {
        return -1;
    }
}