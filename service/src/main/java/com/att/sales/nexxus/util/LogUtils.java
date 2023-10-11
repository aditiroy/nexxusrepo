package com.att.sales.nexxus.util;

import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * @author KRani Utility - log messages to avoid vulnerabilities.
 */
public class LogUtils {
	
	private LogUtils() {
		// empty constructor to suppress default constructor
	}

	/**
	 * Log String.
	 * @param message
	 * @return
	 */
	public static String logString(String message) {
		if (!StringUtils.isEmpty(message)) {
			return HtmlUtils.htmlEscape(message);
		} else {
			return null;
		}
	}
	
	public static String logStringWithAdditionalMessage(String message) {
		if (!StringUtils.isEmpty(message)) {
			String newMessage = message.concat("NEXXUS");
			return HtmlUtils.htmlEscape(newMessage);
		} else {
			return null;
		}
	}
	
	public static void logExecutionDurationMs(Logger logger, String action, long startTime) {
		logger.info("Execution duration: {} took {} ms", action, System.currentTimeMillis() - startTime);
	}
	
	public static void logExecutionDurationMs(Logger logger, long duration, String action) {
		logger.info("Execution duration: {} took {} ms", action, duration);
	}
}
