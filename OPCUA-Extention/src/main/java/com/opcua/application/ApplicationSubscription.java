package com.opcua.application;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.opcua.util.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationSubscription {
	
	@Qualifier("taskScheduler")
	private final ScheduledExecutorService taskSchedulerService;
	private final Environment environment;
	public MicroserviceCredentials cred;
	
	@Autowired
	private Util util;
	
	@EventListener()
    private void startEvent(final MicroserviceSubscriptionAddedEvent event) {
		String url = "";
		if (System.getenv("C8Y_BASEURL") == null) {
			url = environment.getProperty("C8Y.baseURL", "");
		} else {
			url = System.getenv("C8Y_BASEURL");
		}
		log.debug("c8y_baseUrl: "+ url);
		cred = event.getCredentials();
		util.setTenantURL(url);
		util.setTenant(cred.getTenant());
		util.setUsername(cred.getUsername());
		util.setPass(cred.getPassword());
	}
	
}
