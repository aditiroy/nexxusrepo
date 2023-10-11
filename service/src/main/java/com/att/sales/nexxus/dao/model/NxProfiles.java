package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_PROFILES")
@Getter
@Setter
public class NxProfiles implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQUENCE_NX_PROFLIES", sequenceName = "SEQ_NX_PROFILE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_NX_PROFLIES")
	@Column(name = "PROFILE_ID")
	private Long profileId;
	
	@Column(name = "PROFILE_NAME")
	private String profileName;
	
	@OneToMany(mappedBy = "nxProfiles", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<NxUser> nxUsers = new ArrayList<NxUser>();
	
	@Column(name = "ACTIVE")
	private String active;
	
}
