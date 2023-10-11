package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="MP_POP_VENDOR_MAPPING")
@Getter
@Setter

public class MpPopVendorMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	/** The POPCLLI */
	@Column(name="POPCLLI")
	private String popclli;

	/** The VENDOR NAME. */
	@Column(name="VENDOR_NAME")
	private String vendorName;

	/** The SWCCLI */
	@Column(name="SWCCLI")
	private String swccli;

}
