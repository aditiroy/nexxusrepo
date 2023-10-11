package com.att.sales.nexxus.edf.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
public class InputDetailModel {

	private String dunsNumber;

	private String l5MasterAcctId;

	private String l4AcctId;

	private String l3SubAcctId;

}