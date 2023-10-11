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
 * @author ar896d
 *
 */
@Entity
@Table(name="NX_TDM_KEYID_MAPPING")
@Getter
@Setter

public class NxTDMKeyIdMapping implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name ="SEQUENCE_TDM_KEYMAPPING", sequenceName = "SEQUENCE_TDM_KEYMAPPING_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_TDM_KEYMAPPING")
	@Column(name = "NX_TDM_ID")
	private Long nxTdmId;
	
	@Column(name = "NX_GRP_ID")
	private Long nxGrpId;

	@Column(name = "NX_KEYID")
	private String nxKeyId;

	@Column(name = "NEW_NX_KEY_ID")
	private String newNxKeyId;
	
	@Column(name = "QUANTITY")
	private String quantity;
	
	@Column(name = "TDM_NX_KEYID")
	private String tdmNxKeyId;

	@Column(name = "NXT1_CKT_ID")
	private String nxt1CktId;
	

}
