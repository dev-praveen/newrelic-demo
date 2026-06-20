package com.praveen.learn.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhoneDto(
		String id,
		String name,
		Data data
) {

	public record Data(
			Integer year,
			Double price,
			@JsonProperty("CPU model") String cpuModel,
			@JsonProperty("Hard disk size") String hardDiskSize
	) {}

}

