/*
 * package com.att.echo;
 * 
 * import org.apache.camel.Exchange; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory;
 * 
 * 
 *//**
	 * class EchoGroovy.
	 */
/*
 * public class EchoGroovy {
 * 
 *//** The log. */
/*
 * private static Logger log = LoggerFactory.getLogger(EchoGroovy.class);
 * 
 *//**
	 * processes exchange.
	 *
	 * @param exchange the exchange
	 *//*
		 * public void process(Exchange exchange){ exchange.setOut(exchange.getIn());
		 * String res = exchange.getIn().getBody(String.class); String serviceName =
		 * (String)exchange.getOut().getHeader("serviceName"); String serviceVersion =
		 * (String)exchange.getOut().getHeader("serviceVersion");
		 * exchange.getOut().setHeader("responseMessage", "Echo... Successfully Tested "
		 * + serviceName +" Service Version : " + serviceVersion);
		 * 
		 * log.debug(res); } }
		 */
