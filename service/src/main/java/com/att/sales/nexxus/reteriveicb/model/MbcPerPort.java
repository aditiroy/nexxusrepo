package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class MbcPerPort.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MbcPerPort {

	/** The port access type. */
	private String portAccessType;
	
	/** The mbc. */
	private Long mbc;
	
	/**
	 * Gets the port access type.
	 *
	 * @return the port access type
	 */
	public String getPortAccessType() {
		return portAccessType;
	}
	
	/**
	 * Sets the port access type.
	 *
	 * @param portAccessType the new port access type
	 */
	public void setPortAccessType(String portAccessType) {
		this.portAccessType = portAccessType;
	}
	
	/**
	 * Gets the mbc.
	 *
	 * @return the mbc
	 */
	public Long getMbc() {
		return mbc;
	}
	
	/**
	 * Sets the mbc.
	 *
	 * @param mbc the new mbc
	 */
	public void setMbc(Long mbc) {
		this.mbc = mbc;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MbcPerPort [portAccessType:"
				+ portAccessType + ", mbc=" + mbc;
	}
}
