package com.att.sales.nexxus.inr;

import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class OutputJsonFallOutData.
 */
public class OutputJsonFallOutData {

	/** The nx output bean. */
	private NxOutputBean nxOutputBean;

	/** The fall out data. */
	private String fallOutData;

	private JsonNode mpOutputJson;

	private boolean isBeanOutput;

	private boolean hasValue;

	/**
	 * Instantiates a new output json fall out data.
	 *
	 * @param nxOutputBean the nx output bean
	 * @param fallOutData  the fall out data
	 */
	public OutputJsonFallOutData(NxOutputBean nxOutputBean, String fallOutData, JsonNode mpOutputJson,
			boolean isBeanOutput, boolean hasValue) {
		super();
		this.nxOutputBean = nxOutputBean;
		this.fallOutData = fallOutData;
		this.mpOutputJson = mpOutputJson;
		this.isBeanOutput = isBeanOutput;
		this.hasValue = hasValue;
	}

	/**
	 * Gets the nx output bean.
	 *
	 * @return the nx output bean
	 */
	public NxOutputBean getNxOutputBean() {
		return nxOutputBean;
	}

	/**
	 * Gets the fall out data.
	 *
	 * @return the fall out data
	 */
	public String getFallOutData() {
		return fallOutData;
	}

	public JsonNode getMpOutputJson() {
		return mpOutputJson;
	}

	public boolean isBeanOutput() {
		return isBeanOutput;
	}

	public boolean hasValue() {
		return hasValue;
	}
}
