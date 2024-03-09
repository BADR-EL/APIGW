package com.example.apigw.util;

import com.example.apigw.model.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class AuthUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    public static String getToken(String userName, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userName",userName);
        headers.set("role",role);
        HttpEntity<Credential> request = new HttpEntity<>(
                new Credential("admin", "admin"),headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8088/login", HttpMethod.POST,request,String.class);
        System.out.println("token:"+response.getBody());
        return response.getBody();
    }
}
