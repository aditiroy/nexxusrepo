package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_FEATURE")
@Getter
@Setter

public class NxFeature implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "FEATURE_ID")
	private String featureId;
	
	@Column(name = "FEATURE_NAME")
	private String featureName;
	
	@Column(name = "ACTIVE")
	private String active;
	

}
