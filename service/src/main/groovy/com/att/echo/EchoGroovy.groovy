package com.att.echo;

import org.apache.camel.Exchange
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public class EchoGroovy {

	private static Logger log = LoggerFactory.getLogger(EchoGroovy.class);
	
	public void process(Exchange exchange) throws Exception {
		exchange.setOut(exchange.getIn());
		String res = exchange.getIn().getBody(String.class)
		String serviceName = exchange.getOut().getHeader("serviceName");
		String serviceVersion = exchange.getOut().getHeader("serviceVersion");
		exchange.getOut().setHeader("responseMessage", "Echo... Successfully Tested " + serviceName +" Service Version : " + serviceVersion);
		//throw new Exception("Exception test");
		log.debug(res);			
	}	
}
