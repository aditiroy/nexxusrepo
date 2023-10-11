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
 * @author rc9330
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SUBMIT_TO_ANOTHER_DEAL")
public class SubmitToAnotherDeal implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "sequence_submit_to_another_deal", sequenceName = "SEQ_SUBMIT_TO_ANOTHER_DEAL", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_submit_to_another_deal")
	@Column(name = "ID")
	private Long id;

	@Column(name = "DEAL_ID")
	private String dealId;

	@Override
	public String toString() {
		return "SubmitToAnotherDeal [id=" + id + ", dealId=" + dealId + "]";
	}

	

	
	
}
