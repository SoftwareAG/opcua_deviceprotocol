package com.opcua.dto;

import java.util.List;

import lombok.Data;

@Data
public class ApplyConstraints {
	private String browsePathMatchesRegex;
	private List<String> matchesNodeIds;
	private List<String> matchesServerIds;
	private String serverObjectHasFragment;
}
