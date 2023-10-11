package com.att.sales.nexxus.dmaap.mr.util;

import java.io.IOException;

/**
 * The Interface IDmaapMRPublisher.
 */
public interface IDmaapMRPublisher {
	
	/**
	 * Publish message.
	 *
	 * @param topic the topic
	 * @param jsonMessage the json message
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//public String publishMessage(String topic, String jsonMessage) throws IOException;
	
	/**
	 * Publish message.
	 *
	 * @param topic the topic
	 * @param groupName the group name
	 * @param jsonMessage the json message
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String publishMessage(String topic,String groupName, String jsonMessage, String host) throws IOException;
	
}
