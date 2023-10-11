package com.att.sales.nexxus.util;

public class NumUtil {
	
	private NumUtil() {
		//empty constructor
	}
	
	public static long parseLong(String s, long def) {
		try {
			return Long.valueOf(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	public static long parseLong(Long l, long def) {
		if (l == null) {
			return def;
		} else {
			return l.longValue();
		}
	}
}
