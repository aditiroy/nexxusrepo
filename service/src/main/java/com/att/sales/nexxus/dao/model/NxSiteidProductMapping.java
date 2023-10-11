/**
 * 
 */
package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sj0546
 *
 */
@Entity
@Table(name="NX_SITEID_PRODUCT_MAPPING")
@Getter
@Setter
public class NxSiteidProductMapping implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQUENCE_NX_SITEID_PRODUCT", sequenceName = "SEQ_NX_SITEID_PRODUCT_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_NX_SITEID_PRODUCT")
	@Column(name = "NX_SITEID_PRODUCT_ID")
	private Long nxSiteidProductId;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;
	
	@Column(name = "NX_SITEID")
	private String nxSiteId;

}
