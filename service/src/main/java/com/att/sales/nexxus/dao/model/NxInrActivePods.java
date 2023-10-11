package com.att.sales.nexxus.dao.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="NX_INR_ACTIVE_PODS")
@Getter
@Setter
public class NxInrActivePods {
	
	@Id
	@Column(name = "POD_NAME")
	private String podName;
	
	@Column(name = "LAST_HEARTBEAT")
	private Date lastHeartbeat;
	
	@Column(name = "POD_TYPE")
	private String podType; 

}
