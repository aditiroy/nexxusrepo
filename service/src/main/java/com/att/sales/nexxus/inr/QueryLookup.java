package com.att.sales.nexxus.inr;

import java.util.List;
import java.util.Map;

import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;

/**
 * The Class QueryLookup.
 */
public class QueryLookup {
	
	/** The required fields. */
	private List<String> requiredFields;
	
	/** The query mapping. */
	private Map<String, NxKeyFieldPathModel> queryMapping;
	
	/** The lookup entity. */
	private NxLineItemLookUpFieldModel lookupEntity;

	/**
	 * Instantiates a new query lookup.
	 *
	 * @param requiredFields the required fields
	 * @param queryMapping the query mapping
	 * @param lookupEntity the lookup entity
	 */
	public QueryLookup(List<String> requiredFields, Map<String, NxKeyFieldPathModel> queryMapping,
			NxLineItemLookUpFieldModel lookupEntity) {
		super();
		this.requiredFields = requiredFields;
		this.queryMapping = queryMapping;
		this.lookupEntity = lookupEntity;
	}

	/**
	 * Gets the required fields.
	 *
	 * @return the required fields
	 */
	public List<String> getRequiredFields() {
		return requiredFields;
	}

	/**
	 * Sets the required fields.
	 *
	 * @param requiredFields the new required fields
	 */
	public void setRequiredFields(List<String> requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * Gets the query mapping.
	 *
	 * @return the query mapping
	 */
	public Map<String, NxKeyFieldPathModel> getQueryMapping() {
		return queryMapping;
	}

	/**
	 * Sets the query mapping.
	 *
	 * @param queryMapping the query mapping
	 */
	public void setQueryMapping(Map<String, NxKeyFieldPathModel> queryMapping) {
		this.queryMapping = queryMapping;
	}

	/**
	 * Gets the lookup entity.
	 *
	 * @return the lookup entity
	 */
	public NxLineItemLookUpFieldModel getLookupEntity() {
		return lookupEntity;
	}

	/**
	 * Sets the lookup entity.
	 *
	 * @param lookupEntity the new lookup entity
	 */
	public void setLookupEntity(NxLineItemLookUpFieldModel lookupEntity) {
		this.lookupEntity = lookupEntity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryLookup [requiredFields=").append(requiredFields).append(", queryMapping=")
				.append(queryMapping).append("]");
		return builder.toString();
	}
}
