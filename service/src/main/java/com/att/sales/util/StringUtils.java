/**
 * 
 */
package com.att.sales.util;

import org.apache.commons.codec.binary.Base64;

import com.att.sales.framework.util.swagger.APIConstants;


/**
 * The Class StringUtils.
 *
 * @author rk967c
 */
public class StringUtils implements APIConstants {
	
	/**
	 * Instantiates a new string utils.
	 */
	private StringUtils() {
		throw new IllegalAccessError("Utility class");
	}

	/**
	 * Creates the basic encoding.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @return createBasicEncoding
	 * login credentials like user name and password need to pass in-order to 
	 * create base64 encoded string.
	 */
	public static String createBasicEncoding(String userName, String password) {
		String authorizationValue = null;
		if (userName != null && userName.length() > 0 && password != null && password.length() > 0) {
			String authString = userName + ":" + password;
			authorizationValue = "Basic "
					+ new String(Base64.encodeBase64(authString.getBytes()));
		}
		return authorizationValue;
	}
	
	public static String trimLeadingZeros(String source) {
		for (int i = 0; i < source.length(); ++i) {
	        char c = source.charAt(i);
	        if (c != '0') {
	            return source.substring(i);
	        }
	    }
		if (source.length() == 0) {
			return source;
		}
	    return "0";
	}
}
