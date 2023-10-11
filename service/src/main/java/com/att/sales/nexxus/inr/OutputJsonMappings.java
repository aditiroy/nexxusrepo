package com.att.sales.nexxus.inr;

import java.util.List;

public class OutputJsonMappings {
	private List<OutputJsonMapping> mappings;
	private String arrayName;
	private String destName;

	public List<OutputJsonMapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<OutputJsonMapping> mappings) {
		this.mappings = mappings;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public String getArrayName() {
		return arrayName;
	}

	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OutputJsonMappings [mappings=").append(mappings).append(", arrayName=").append(arrayName)
				.append(", destName=").append(destName).append("]");
		return builder.toString();
	}

}
