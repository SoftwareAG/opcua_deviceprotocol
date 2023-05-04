package com.opcua.dto;

import lombok.Data;

@Data
public class AlarmCreation {
	private String type;
    private String severity;
    private String text;
    private String status;
}
