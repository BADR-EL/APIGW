package com.example.apigw.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

public class RouteValidator {
    public List<String> unprotectedURLs;

    public RouteValidator(List<String> urls){
        this.unprotectedURLs = urls;
    }

    public Predicate<ServerHttpRequest> isSecured = request -> unprotectedURLs.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
