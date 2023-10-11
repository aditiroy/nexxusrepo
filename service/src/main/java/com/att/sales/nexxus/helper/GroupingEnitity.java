package com.att.sales.nexxus.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupingEnitity {

	private Map<String,Set<String>> attributes;
	public GroupingEnitity() {
		this.attributes=new HashMap<String,Set<String>>();
	}
	
	public void setValue(String fieldName,Set<String> values) {
		attributes.put(fieldName, values);
	}
	
	public Set<String> getValue(String fieldName) {
		return attributes.get(fieldName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupingEnitity other = (GroupingEnitity) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		return true;
	}
	
	
}
