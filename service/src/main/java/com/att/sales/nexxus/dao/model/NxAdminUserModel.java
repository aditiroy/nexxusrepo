package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Shrinath
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_ADMIN_USER")
public class NxAdminUserModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_admin_user", sequenceName = "SEQ_NX_ADMIN_USER", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_admin_user")

	@Column(name = "ID")
	private Long id;

	@Column(name = "ATTUID")
	private String attuid;

	@Column(name = "FNAME")
	private String fname;

	@Column(name = "MNAME")
	private String mname;

	@Column(name = "LNAME")
	private String lname;
	
	@Column(name = "PHONE")
	private String phone;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "ROLE")
	private String role;
	
	@Column(name = "ACTIVE_YN")
	private String activeYn;

}


