package com.example.apigw.filter;

import com.example.apigw.model.AuthConfig;
import com.example.apigw.model.AuthMode;
import com.example.apigw.util.AuthUtil;
import com.example.apigw.util.JWTUtil;
import com.example.apigw.validator.RouteValidator;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthFilterFactory {

    public static GatewayFilter create(AuthConfig config) {
        return new AuthFilter(config);
    }

    private static class AuthFilter implements GatewayFilter {
        RouteValidator routeValidator;
        boolean authEnabled;

        public AuthFilter(AuthConfig config) {
            this.routeValidator = new RouteValidator(config.getUnprotectedURLs());
            this.authEnabled = config.getAuthEnabled() == AuthMode.ENABLED;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if(!authEnabled) {
                System.out.println("Authentication is disabled. To enable it, make AuthMode = ENABLED");
                return chain.filter(exchange);
            }
            String token ="";
            ServerHttpRequest request = exchange.getRequest();

            if(routeValidator.isSecured.test(request)) {
                System.out.println("validating authentication token");
                if(this.isCredsMissing(request)) {
                    System.out.println("Credentials missing");
                    return this.onError(exchange,"Credentials missing", HttpStatus.UNAUTHORIZED);
                }
                if (request.getHeaders().containsKey("userName") && request.getHeaders().containsKey("role")) {
                    token = AuthUtil.getToken(request.getHeaders().get("userName").toString(), request.getHeaders().get("role").toString());
                }
                else {
                    token = request.getHeaders().get("Authorization").toString().split(" ")[1];
                }

                if(JWTUtil.isInvalid(token)) {
                    return this.onError(exchange,"Auth header invalid",HttpStatus.UNAUTHORIZED);
                }
                else {
                    System.out.println("Authentication is successful");
                }

                this.populateRequestWithHeaders(exchange,token);
            }
            return chain.filter(exchange);
        }

        private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(httpStatus);
            return response.setComplete();
        }

        private String getAuthHeader(ServerHttpRequest request) {
            return  request.getHeaders().getOrEmpty("Authorization").get(0);
        }


        private boolean isCredsMissing(ServerHttpRequest request) {
            return !(request.getHeaders().containsKey("userName") && request.getHeaders().containsKey("role")) && !request.getHeaders().containsKey("Authorization");
        }

        private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
            Claims claims = JWTUtil.getALlClaims(token);
            exchange.getRequest()
                    .mutate()
                    .header("id",String.valueOf(claims.get("id")))
                    .header("role", String.valueOf(claims.get("role")))
                    .build();
        }
    }
}
