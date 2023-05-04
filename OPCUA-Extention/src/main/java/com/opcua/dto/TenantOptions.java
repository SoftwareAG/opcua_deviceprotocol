package com.opcua.dto;

import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
public class TenantOptions {
	private String tenant_base_url;
	private String username;
	private String pass;
	private String tenant;
}
