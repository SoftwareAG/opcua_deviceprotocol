package com.opcua.dto;

import lombok.Data;

@Data
public class SubscriptionType {
	private String type;
    private SubscriptionParameters subscriptionParameters;
    private int rate;
}
