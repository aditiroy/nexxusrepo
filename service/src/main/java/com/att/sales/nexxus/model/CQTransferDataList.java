package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

/**
 * The Class CQTransferDataList.
 *
 * @author km017g
 */
public class CQTransferDataList implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The result id. */
	private String resultId;
	
	/** The cq transfer data. */
	private String cqTransferData;
	
	/** The us eth serv avail check data. */
	private List<String> usEthServAvailCheckData;
	
	/** The us esp prod override data. */
	private List<String> usEspProdOverrideData;
	
	/**
	 * Gets the result id.
	 *
	 * @return the result id
	 */
	public String getResultId() {
		return resultId;
	}
	
	/**
	 * Sets the result id.
	 *
	 * @param resultId the new result id
	 */
	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	
	/**
	 * Gets the cq transfer data.
	 *
	 * @return the cq transfer data
	 */
	public String getCqTransferData() {
		return cqTransferData;
	}
	
	/**
	 * Sets the cq transfer data.
	 *
	 * @param cqTransferData the new cq transfer data
	 */
	public void setCqTransferData(String cqTransferData) {
		this.cqTransferData = cqTransferData;
	}
	
	/**
	 * Gets the us eth serv avail check data.
	 *
	 * @return the us eth serv avail check data
	 */
	public List<String> getUsEthServAvailCheckData() {
		return usEthServAvailCheckData;
	}
	
	/**
	 * Sets the us eth serv avail check data.
	 *
	 * @param usEthServAvailCheckData the new us eth serv avail check data
	 */
	public void setUsEthServAvailCheckData(List<String> usEthServAvailCheckData) {
		this.usEthServAvailCheckData = usEthServAvailCheckData;
	}
	
	/**
	 * Gets the us esp prod override data.
	 *
	 * @return the us esp prod override data
	 */
	public List<String> getUsEspProdOverrideData() {
		return usEspProdOverrideData;
	}
	
	/**
	 * Sets the us esp prod override data.
	 *
	 * @param usEspProdOverrideData the new us esp prod override data
	 */
	public void setUsEspProdOverrideData(List<String> usEspProdOverrideData) {
		this.usEspProdOverrideData = usEspProdOverrideData;
	}
	
	
	

}
