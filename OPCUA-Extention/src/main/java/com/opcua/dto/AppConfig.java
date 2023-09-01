package com.opcua.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="user")
public class AppConfig {
	
	@Value("${C8Y.baseURL}")
    private String baseUrl;

	@Value("${C8Y.bootstrap.tenant}")
    public String tenant;
	
	@Value("${C8Y.bootstrap.user}")
    private String user;
	
	@Value("${C8Y.bootstrap.password}")
    private String password;
	
}
