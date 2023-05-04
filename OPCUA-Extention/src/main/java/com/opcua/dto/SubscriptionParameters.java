package com.opcua.dto;

import lombok.Data;

@Data
public class SubscriptionParameters {
	 private int samplingRate;
	 private String dataChangeTrigger;
	 private String deadbandType;
	 private boolean discardOldest;
	 private int queueSize;
	 private String ranges;
}
