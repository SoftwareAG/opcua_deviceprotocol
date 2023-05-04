package com.opcua.dto;

import lombok.Data;

@Data
public class DeviceProtocolInput {
	private String deviceprotocolname;
	private String tagname;
	private String datatype; 
	private String type; 
	private String text; 
	private String text2; 
	private String unit; 
	private String enabled; 
	private String serverid; 
	private String servername; 
	private String subscriptiontype; 
	private String samplingperiod; 
}
