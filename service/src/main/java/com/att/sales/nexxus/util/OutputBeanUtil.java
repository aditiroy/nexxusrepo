package com.att.sales.nexxus.util;

/**
 * The Class OutputBeanUtil.
 */
public class OutputBeanUtil {
	
	/**
	 * Instantiates a new output bean util.
	 */
	private OutputBeanUtil() {
		//hide public constructor
	}
	
	/**
	 * Gets the location.
	 *
	 * @param state the state
	 * @return the location
	 */
	public static String getLocation(String state) {
		if (state == null) {
			return null;
		}
		String location = "1 Lower 48";
		if (state.equalsIgnoreCase("AK") || state.equalsIgnoreCase("Alaska")) {
			location = "2 Alaska";
		} else if (state.equalsIgnoreCase("HI") || state.equalsIgnoreCase("Hawaii")) {
			location = "3 Hawaii";
		} else if (state.equalsIgnoreCase("VI") || state.equalsIgnoreCase("USVI")
				|| state.equalsIgnoreCase("US Vergin Islands")) {
			location = "4 USVI";
		} else if (state.equalsIgnoreCase("PR") || state.equalsIgnoreCase("Puerto Rico")) {
			location = "5 Puerto Rico";
		}
		return location;
	}
}
