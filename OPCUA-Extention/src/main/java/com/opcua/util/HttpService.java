package com.opcua.util;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.opcua.dto.DeviceProtocol;
import com.opcua.dto.TenantOptions;

@Service
public class HttpService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private TenantOptions tenantOptions;
	
	HttpHeaders headers = new HttpHeaders();
	HttpEntity<String> entity = null;
	
	public ResponseEntity<String> createDeviceProtocol(DeviceProtocol deviceProtocol) {
		String originalInput = tenantOptions.getTenant()+"/"+tenantOptions.getUsername()+":"+tenantOptions.getPass();
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers1.set("Authorization", "Basic "+encodedString);
		HttpEntity<Object> entity1 = new HttpEntity<Object>(deviceProtocol, headers1);
		
		return restTemplate.exchange(tenantOptions.getTenant_base_url() +"/service/opcua-mgmt-service/deviceTypes/", HttpMethod.POST, entity1, String.class);
	}
	
	public ResponseEntity<List<DeviceProtocol>> getDeviceProtocol() {
		String originalInput = tenantOptions.getTenant()+"/"+tenantOptions.getUsername()+":"+tenantOptions.getPass();
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers1.set("Authorization", "Basic "+encodedString);
		HttpEntity<Object> entity1 = new HttpEntity<Object>(headers1);
		
		return restTemplate.exchange(tenantOptions.getTenant_base_url() +"/service/opcua-mgmt-service/deviceTypes/", HttpMethod.GET, entity1, new ParameterizedTypeReference<List<DeviceProtocol>>() {});
	}
	
	public ResponseEntity<String> updateDeviceProtocol(DeviceProtocol deviceProtocol) {
		String originalInput = tenantOptions.getTenant()+"/"+tenantOptions.getUsername()+":"+tenantOptions.getPass();
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers1.set("Authorization", "Basic "+encodedString);
		HttpEntity<Object> entity1 = new HttpEntity<Object>(deviceProtocol, headers1);
		
		return restTemplate.exchange(tenantOptions.getTenant_base_url() +"/service/opcua-mgmt-service/deviceTypes/"+deviceProtocol.getId(), HttpMethod.PUT, entity1, String.class);
	}
	
}