package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_MYLOGIN_MAPPING")
public class NxMpMyloginMapping implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "PROFILE_NAME")
	private String profileName;
	
	@Column(name = "HEADER_NAME")
	private String headerName;
	
	@Column(name = "VARIABLE_NAME")
	private String variableName;
	
	@Column(name = "ACTIVE")
	private String active;
	
	@Column(name="ORDER_SEQ")
	private Integer orderSeq;
	
	@Column(name = "QUOTE")
	private String quote;
	
	@Column(name = "CONSTANT_VALUE")
	private String constantValue;
	
}