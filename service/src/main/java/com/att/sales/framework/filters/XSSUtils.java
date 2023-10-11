package com.att.sales.framework.filters;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;

@Component
public class XSSUtils {
	public static String stripXSS(String value) {
	    if (value == null) {
	        return null;
	    }
	    value = ESAPI.encoder()
	      .canonicalize(value)
	      .replaceAll("\0", "");
	    return Jsoup.clean(value, Whitelist.basic()).replaceAll("&amp;", "&");
	}
	
}

