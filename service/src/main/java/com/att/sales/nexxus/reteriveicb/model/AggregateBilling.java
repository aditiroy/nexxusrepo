package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;



/**
 * The Class AggregateBilling.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AggregateBilling {
	
	/** The aggregate port id. */
	private Long aggregatePortId;
	
	/** The mbc per port. */
	private List<MbcPerPort> mbcPerPort;
	
	/** The usage tiers. */
	private List<UsageTiers> usageTiers;
	
	/**
	 * Gets the aggregate port id.
	 *
	 * @return the aggregate port id
	 */
	public Long getAggregatePortId() {
		return aggregatePortId;
	}

	/**
	 * Sets the aggregate port id.
	 *
	 * @param aggregatePortId the new aggregate port id
	 */
	public void setAggregatePortId(Long aggregatePortId) {
		this.aggregatePortId = aggregatePortId;
	}

	/**
	 * Gets the mbc per port.
	 *
	 * @return the mbc per port
	 */
	public List<MbcPerPort> getMbcPerPort() {
		return mbcPerPort;
	}

	/**
	 * Sets the mbc per port.
	 *
	 * @param mbcPerPort the new mbc per port
	 */
	public void setMbcPerPort(List<MbcPerPort> mbcPerPort) {
		this.mbcPerPort = mbcPerPort;
	}

	/**
	 * Gets the usage tiers.
	 *
	 * @return the usage tiers
	 */
	public List<UsageTiers> getUsageTiers() {
		return usageTiers;
	}

	/**
	 * Sets the usage tiers.
	 *
	 * @param usageTiers the new usage tiers
	 */
	public void setUsageTiers(List<UsageTiers> usageTiers) {
		this.usageTiers = usageTiers;
	}
}
