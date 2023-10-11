package com.att.sales.nexxus.inr;

public class OutputJsonMapping {
	public static final String SOURCE_LINE_ITEM_QUERY = "sourceLineItemQuery";
	private String type;
	private String sourceField;
	private String destField;
	private String source;
	private String destName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceField() {
		return sourceField;
	}

	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}

	public String getDestField() {
		return destField;
	}

	public void setDestField(String destField) {
		this.destField = destField;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OutputJsonMapping [type=").append(type).append(", sourceField=").append(sourceField)
				.append(", destField=").append(destField).append(", source=").append(source).append(", destName=")
				.append(destName).append("]");
		return builder.toString();
	}

}
