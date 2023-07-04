package com.opcua;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@MicroserviceApplication
@Configuration
@EnableCaching
public class OpcuaExtention {

	public static void main(String[] args) {
		SpringApplication.run(OpcuaExtention.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
       return builder.build();
    }
	
    @Bean
	@Qualifier("taskScheduler")
	public ScheduledExecutorService taskScheduler() {
		return Executors.newScheduledThreadPool(25, new ThreadFactoryBuilder().setNameFormat("task-scheduler-%s").setDaemon(true).build());
	}

}
