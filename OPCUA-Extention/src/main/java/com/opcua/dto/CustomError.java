package com.opcua.dto;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class CustomError {
	
	final private String type = "Error";
	private HttpStatus status;
	private String message;

	public CustomError(HttpStatus status,String message) {
		this.status = status;
		this.message = message;
	}
}
