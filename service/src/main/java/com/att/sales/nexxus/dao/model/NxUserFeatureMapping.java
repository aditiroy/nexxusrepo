package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_USER_FEATURE_MAPPING")
@Getter
@Setter
public class NxUserFeatureMapping implements Serializable {

private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	private Long Id;
	
	@Column(name = "ENABLED")
	private String enabled;
	
	@ManyToOne
	@JoinColumn(name = "FEATURE_ID")
	private NxFeature nxFeature;
	
	@ManyToOne
	@JoinColumn(name = "USER_ATT_ID")
	private NxUser nxUser ;
	
}
