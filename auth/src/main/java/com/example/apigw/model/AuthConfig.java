package com.example.apigw.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthConfig {
    AuthMode authEnabled;
    List<String> unprotectedURLs;
}
