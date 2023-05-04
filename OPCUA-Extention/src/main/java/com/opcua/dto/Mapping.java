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
	
	 @Override
     public boolean equals(Object obj) {
             if (this == obj)
                     return true;
             if (obj == null)
                     return false;
             if (getClass() != obj.getClass())
                     return false;
             Mapping mapping = (Mapping) obj;
             if (name == null) {
                     if (mapping.name != null)
                             return false;
             } else if (!name.equals(mapping.name))
                     return false;
             return true;
     }
}
