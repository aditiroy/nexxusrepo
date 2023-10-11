package com.att.sales.nexxus.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtil {

	private static Pattern YYYY_MM_DD = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\d");
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a");

	private RegExUtil() {
		// empty constructor
	}

	public static String firstMatchYYYY_MM_DD(String in) {
		String res = null;
		Matcher matcher = YYYY_MM_DD.matcher(in);
		if (matcher.find()) {
			res = matcher.group();
		}
		return res;
	}

	public static String convertDateToYYYY_MM_DD(String in) {
		String res = null;
		try {
			LocalDate localDate = LocalDate.parse(in, formatter);
			return localDate.toString();
		} catch (DateTimeParseException e) {

		}

		return res;
	}
}
