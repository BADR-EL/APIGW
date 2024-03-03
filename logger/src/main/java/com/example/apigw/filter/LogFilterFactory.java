package com.example.apigw.filter;

import com.example.apigw.model.LogConfig;
import com.example.apigw.model.LogLevel;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;

@Component
public class LogFilterFactory {

    public static GatewayFilter create(LogConfig config) {
        return new CustomGatewayFilter(config);
    }

    private static class CustomGatewayFilter implements GatewayFilter, Ordered {

        private final LogConfig config;

        public CustomGatewayFilter(LogConfig config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            logRequest(exchange);
            return logResponse(exchange, chain);
        }

        private void logRequest(ServerWebExchange exchange) {
            if (config.getRequestLogger() == LogLevel.ENABLED) {
                Object body = exchange.getAttribute("cachedRequestBodyObject");
                System.out.println("Request Body: " + body);
            }
        }

        private Mono<Void> logResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (config.getResponseLogger() == LogLevel.ENABLED) {
                String path = exchange.getRequest().getPath().toString();
                ServerHttpResponse response = exchange.getResponse();
                ServerHttpRequest request = exchange.getRequest();
                DataBufferFactory dataBufferFactory = response.bufferFactory();
                ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(path, response, request, dataBufferFactory);
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);
        }

        private ServerHttpResponseDecorator getDecoratedResponse(String path, ServerHttpResponse response, ServerHttpRequest request, DataBufferFactory dataBufferFactory) {
            return new ServerHttpResponseDecorator(response) {

                @Override
                public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                            byte[] content = new byte[joinedBuffers.readableByteCount()];
                            joinedBuffers.read(content);
                            String responseBody = new String(content, StandardCharsets.UTF_8);
                            logResponse(request, responseBody);
                            return dataBufferFactory.wrap(responseBody.getBytes());
                        })).onErrorResume(err -> {
                            System.out.println("Error while decorating Response: " + err.getMessage());
                            return Mono.empty();
                        });
                    }
                    return super.writeWith(body);
                }
            };
        }

        private void logResponse(ServerHttpRequest request, String responseBody) {
            System.out.println("Request ID: " + request.getId() +
                    ", Method: " + request.getMethod().name() +
                    ", Request URL: " + request.getURI() +
                    ", Response Body: " + responseBody);
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}

