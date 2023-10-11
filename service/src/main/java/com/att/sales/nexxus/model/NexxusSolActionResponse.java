package com.att.sales.nexxus.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.NxLookupDataModel;

import lombok.Getter;
import lombok.Setter;
/**
 * @author sx623g
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class NexxusSolActionResponse extends ServiceResponse {
	private static final long serialVersionUID = 1L;
	private Long nxSolutionId;
	private List<AuditDetails> audit;
	private Map<String,NxLookupDataModel> nxLookupDataList;
}
