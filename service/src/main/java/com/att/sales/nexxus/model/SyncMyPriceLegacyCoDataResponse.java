package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SyncMyPriceLegacyCoDataResponse extends ServiceResponse {
	private static final long serialVersionUID = 1L;
	private Integer legacyCoDetailsImportTaskId;
	private String legacyCoDetailsName;
	private Integer legacyCoDetailsDeployTaskId;
	private String legacyCoDetailsMpDeployStatus;
	private Integer legacyCoPercentageImportTaskId;
	private String legacyCoPercentageName;
	private Integer legacyCoPercentageDeployTaskId;
	private String legacyCoPercentageMpDeployStatus;

	public Integer getLegacyCoDetailsImportTaskId() {
		return legacyCoDetailsImportTaskId;
	}

	public void setLegacyCoDetailsImportTaskId(Integer legacyCoDetailsImportTaskId) {
		this.legacyCoDetailsImportTaskId = legacyCoDetailsImportTaskId;
	}

	public String getLegacyCoDetailsName() {
		return legacyCoDetailsName;
	}

	public void setLegacyCoDetailsName(String legacyCoDetailsName) {
		this.legacyCoDetailsName = legacyCoDetailsName;
	}

	public Integer getLegacyCoDetailsDeployTaskId() {
		return legacyCoDetailsDeployTaskId;
	}

	public void setLegacyCoDetailsDeployTaskId(Integer legacyCoDetailsDeployTaskId) {
		this.legacyCoDetailsDeployTaskId = legacyCoDetailsDeployTaskId;
	}

	public String getLegacyCoDetailsMpDeployStatus() {
		return legacyCoDetailsMpDeployStatus;
	}

	public void setLegacyCoDetailsMpDeployStatus(String legacyCoDetailsMpDeployStatus) {
		this.legacyCoDetailsMpDeployStatus = legacyCoDetailsMpDeployStatus;
	}

	public Integer getLegacyCoPercentageImportTaskId() {
		return legacyCoPercentageImportTaskId;
	}

	public void setLegacyCoPercentageImportTaskId(Integer legacyCoPercentageImportTaskId) {
		this.legacyCoPercentageImportTaskId = legacyCoPercentageImportTaskId;
	}

	public String getLegacyCoPercentageName() {
		return legacyCoPercentageName;
	}

	public void setLegacyCoPercentageName(String legacyCoPercentageName) {
		this.legacyCoPercentageName = legacyCoPercentageName;
	}

	public Integer getLegacyCoPercentageDeployTaskId() {
		return legacyCoPercentageDeployTaskId;
	}

	public void setLegacyCoPercentageDeployTaskId(Integer legacyCoPercentageDeployTaskId) {
		this.legacyCoPercentageDeployTaskId = legacyCoPercentageDeployTaskId;
	}

	public String getLegacyCoPercentageMpDeployStatus() {
		return legacyCoPercentageMpDeployStatus;
	}

	public void setLegacyCoPercentageMpDeployStatus(String legacyCoPercentageMpDeployStatus) {
		this.legacyCoPercentageMpDeployStatus = legacyCoPercentageMpDeployStatus;
	}

}
