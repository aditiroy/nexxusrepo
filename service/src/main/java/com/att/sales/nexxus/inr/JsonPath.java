package com.att.sales.nexxus.inr;

/**
 * The Class JsonPath.
 */
public class JsonPath {
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "/";
	public static final String ARRAY_SUFFIX = "_array";
	
	/** The path. */
	private String path;
	
	/**
	 * Instantiates a new json path.
	 *
	 * @param str the str
	 */
	public JsonPath(String str) {
		path = str;
	}
	
	/**
	 * Gets the root path.
	 *
	 * @return the root path
	 */
	public static JsonPath getRootPath() {
		return new JsonPath("");
	}
	
	/**
	 * Resolve container node.
	 *
	 * @param nodeName the node name
	 * @return the json path
	 */
	public JsonPath resolveContainerNode(String nodeName) {
		return new JsonPath(path + SEPARATOR + nodeName);
	}
	
	// should only be used in trimJsonWithTagFilter, otherwise getFieldName(), parent(), isAncestor(), isDescendant() will broken
	public JsonPath resolveContainerNode(int arrayIndex) {
		return new JsonPath(path + SEPARATOR + arrayIndex);
	}
	
	/**
	 * Resolve field.
	 *
	 * @param fieldName the field name
	 * @return the string
	 */
	public String resolveField(String fieldName) {
		return path + SEPARATOR + fieldName;
	}
	
	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public String getFieldName() {
		if (path.length() == 0) {
			return "";
		}
		int index = path.lastIndexOf(SEPARATOR);
		return path.substring(index + 1);
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Parent.
	 *
	 * @return the json path
	 */
	public JsonPath parent() {
		int index = path.lastIndexOf(SEPARATOR);
		if (index < 0) {
			return getRootPath();
		}
		return new JsonPath(path.substring(0, index));
	}
	
	/**
	 * Checks if is ancestor.
	 *
	 * @param other the other
	 * @return true, if is ancestor
	 */
	public boolean isAncestor(JsonPath other) {
		return other.getPath().startsWith(path) && !path.equals(other.getPath());
	}
	
	/**
	 * Checks if is descendant.
	 *
	 * @param other the other
	 * @return true, if is descendant
	 */
	public boolean isDescendant(JsonPath other) {
		return path.startsWith(other.getPath()) && !path.equals(other.getPath());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return path;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JsonPath other = (JsonPath) obj;
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		return true;
	}
}