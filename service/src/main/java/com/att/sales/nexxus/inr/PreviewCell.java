package com.att.sales.nexxus.inr;

/**
 * The Class PreviewCell.
 */
public class PreviewCell {
	
	/** The num val. */
	private Double numVal;
	
	/** The str val. */
	private String strVal;

	/**
	 * Instantiates a new preview cell.
	 *
	 * @param strVal the str val
	 */
	public PreviewCell(String strVal) {
		super();
		this.strVal = strVal;
	}

	/**
	 * Instantiates a new preview cell.
	 *
	 * @param numVal the num val
	 */
	public PreviewCell(Double numVal) {
		super();
		this.numVal = numVal;
	}

	/**
	 * Gets the num val.
	 *
	 * @return the num val
	 */
	public Double getNumVal() {
		return numVal;
	}

	/**
	 * Gets the str val.
	 *
	 * @return the str val
	 */
	public String getStrVal() {
		return strVal;
	}
}
