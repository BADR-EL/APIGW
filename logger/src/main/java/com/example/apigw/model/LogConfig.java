package com.example.apigw.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogConfig {
    private LogLevel requestLogger;
    private LogLevel responseLogger;

}
