package com.example.apigw.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JWTUtil {
    private static final String secret = "VHKJMNnbfhbsjkdbVJHVkhbJBKJBsmfnbngygiyguFYVHJbkjnjnsjdnlkfn";

    public static Claims getALlClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private static boolean isTokenExpired(String token ) {
        return getALlClaims(token).getExpiration().before(new Date());
    }

    public static boolean isInvalid(String token) {
        return isTokenExpired(token);
    }

}
