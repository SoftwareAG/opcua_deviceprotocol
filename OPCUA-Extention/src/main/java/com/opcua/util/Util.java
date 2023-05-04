package com.opcua.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.opcua.dto.CustomError;
import com.opcua.dto.HttpResponse;
import com.opcua.dto.TenantOptions;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Util {
	
	@Autowired
	private TenantOptions tenantOptions;
	
	public ResponseEntity<?> checkMessageAndProcessResponse(String message) {
		
		if (message.toLowerCase().contains("error")) {
			return ResponseEntity.internalServerError().build();
		}
		if (message.toLowerCase().contains("mismatch") || message.toLowerCase().contains("mandatory")) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
					.body(new CustomError(HttpStatus.PRECONDITION_FAILED, message));
		}
		HttpResponse successResponse = new HttpResponse();
		successResponse.setMessage(message);
		return ResponseEntity.ok(successResponse);
		
	}
	

	public void setUsername(String username) {
		log.debug(username);
		tenantOptions.setUsername(username);
	}

	public void setPass(String pass) {
		log.debug(pass);
		tenantOptions.setPass(pass);
	}

	public void setTenant(String tenant) {
		log.debug(tenant);
		tenantOptions.setTenant(tenant);
	}

	public void setTenantURL(String url) {
		log.debug(url);
		tenantOptions.setTenant_base_url(url);
	}
}
