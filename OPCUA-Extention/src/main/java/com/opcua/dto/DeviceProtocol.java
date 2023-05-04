package com.opcua.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class DeviceProtocol {
	private String id;
	private String name;
	private String referencedServerId;
	private String referencedServerName;
	private String referencedRootNodeId;
	private String description;
	private boolean enabled;
	private List<Mapping> mappings;
	private SubscriptionType subscriptionType;
	private ApplyConstraints applyConstraints;
}
