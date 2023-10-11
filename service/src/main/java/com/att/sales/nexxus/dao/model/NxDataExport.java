package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_DATA_EXPORT")
@Getter
@Setter
public class NxDataExport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	
	@Column(name = "COMPONENT")
	private String componenet;
	
	@Column(name = "DISPLAY_NAME")
	private String displayName;
	
	@Column(name = "VARIABLE_NAME")
	private String variableName;
	
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	@Column(name = "ORDER_SEQ")
	private int orderSeq;
	
	@Column(name = "ACTIVE")
	private String active;
}
