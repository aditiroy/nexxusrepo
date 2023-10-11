package com.att.sales.nexxus.dao.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_REQ_REFNUM_MAPPING")
@Getter
@Setter
public class NxReqRefNumMapping {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_req_refnum_map_id", sequenceName = "SEQ_NX_REQ_REFNUM_MAP_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_req_refnum_map_id")
	@Column(name = "NX_REQ_REFNUM_MAPPING_ID")
	private Long nxReqRefNumMappingId;

	@Column(name = "NEXXUS_REF_NUM")
	private String nexxusRefNum;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "NX_REQ_ID")
	private String nxReqId;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "ACTIVE_YN")
	private String activeYn;

	@Column(name = "USRP_REQUEST_OBJ")
	private String usrpRequestObj;

	@Column(name = "USRP_RESPONSE_OBJ")
	private String usrpResponseObj;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "IS_PROCESS")
	private String isProcess;

}
