package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "LEGACY_CO_PERCENTAGE")
public class LegacyCoPercentage implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private Pk pk;

	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "STATUS")
	private String status;

	public Pk getPk() {
		return pk;
	}

	public void setPk(Pk pk) {
		this.pk = pk;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(csvStatus()).append(",").append(csvColumn(pk.coClli1)).append(",").append(csvColumn(pk.coClli2))
				.append(",").append(csvColumn(pk.ocn)).append(",").append(csvColumn(companyName)).append(",")
				.append(csvColumn(pk.percentage));
		return builder.toString();
	}
	
	protected String csvStatus() {
		if (status == null) {
			return "";
		} else if (status.endsWith("modify")) {
			return "modify";
		} else if (status.endsWith("add")) {
			return "modify";
		} else {
			return "";
		}
	}
	
	protected String csvColumn(String value) {
		if (value == null) {
			return "";
		} else if (!value.contains(",")) {
			return value;
		} else {
			return "\"" + value + "\"";
		}
	}

	@Embeddable
	public static class Pk implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Column(name = "CO_CLLI1")
		private String coClli1;
		
		@Column(name = "CO_CLLI2")
		private String coClli2;
		
		@Column(name = "OCN")
		private String ocn;
		
		@Column(name = "PERCENTAGE")
		private String percentage;
		
		public Pk() {
			super();
		}
		
		public Pk(String coClli1, String coClli2, String ocn, String percentage) {
			super();
			this.coClli1 = coClli1;
			this.coClli2 = coClli2;
			this.ocn = ocn;
			this.percentage = percentage;
		}

		public String getCoClli1() {
			return coClli1;
		}

		public void setCoClli1(String coClli1) {
			this.coClli1 = coClli1;
		}

		public String getCoClli2() {
			return coClli2;
		}

		public void setCoClli2(String coClli2) {
			this.coClli2 = coClli2;
		}

		public String getOcn() {
			return ocn;
		}

		public void setOcn(String ocn) {
			this.ocn = ocn;
		}

		public String getPercentage() {
			return percentage;
		}

		public void setPercentage(String percentage) {
			this.percentage = percentage;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((coClli1 == null) ? 0 : coClli1.hashCode());
			result = prime * result + ((coClli2 == null) ? 0 : coClli2.hashCode());
			result = prime * result + ((ocn == null) ? 0 : ocn.hashCode());
			result = prime * result + ((percentage == null) ? 0 : percentage.hashCode());
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
			Pk other = (Pk) obj;
			if (coClli1 == null) {
				if (other.coClli1 != null)
					return false;
			} else if (!coClli1.equals(other.coClli1))
				return false;
			if (coClli2 == null) {
				if (other.coClli2 != null)
					return false;
			} else if (!coClli2.equals(other.coClli2))
				return false;
			if (ocn == null) {
				if (other.ocn != null)
					return false;
			} else if (!ocn.equals(other.ocn))
				return false;
			if (percentage == null) {
				if (other.percentage != null)
					return false;
			} else if (!percentage.equals(other.percentage))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Pk [coClli1=").append(coClli1).append(", coClli2=").append(coClli2).append(", ocn=")
					.append(ocn).append(", percentage=").append(percentage).append("]");
			return builder.toString();
		}
	}
}
