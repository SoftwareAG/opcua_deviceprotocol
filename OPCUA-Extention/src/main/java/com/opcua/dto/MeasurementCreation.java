package com.opcua.dto;

import lombok.Data;

@Data
public class MeasurementCreation {
	private String unit;
	private String type;
	private String fragmentName;
	private String series;
}
