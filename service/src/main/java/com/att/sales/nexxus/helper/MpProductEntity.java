package com.att.sales.nexxus.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MpProductEntity {
	
	private String productDocumentNo;
	
	private String productName;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productDocumentNo == null) ? 0 : productDocumentNo.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MpProductEntity other = (MpProductEntity) obj;
		if (productDocumentNo == null) {
			if (other.productDocumentNo != null)
				return false;
		} else if (!productDocumentNo.equals(other.productDocumentNo))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		return true;
	}
	

}
