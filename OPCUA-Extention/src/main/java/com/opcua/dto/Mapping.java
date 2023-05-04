package com.opcua.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Mapping {
	private List<String> browsePath;
	private MeasurementCreation measurementCreation;
	private EventCreation eventCreation;
	private AlarmCreation alarmCreation;
	private String name;
}
