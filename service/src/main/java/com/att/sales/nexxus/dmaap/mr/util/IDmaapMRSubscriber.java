package com.att.sales.nexxus.dmaap.mr.util;

import java.io.IOException;
import java.util.List;

/**
 * IDmaapMRSubscriber.
 *
 * @author 
 */
public interface IDmaapMRSubscriber {
	
	/**
	 * retrieveMessage.
	 *
	 * @param topic the topic
	 * @param groupName the group name
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws Exception the exception
	 */
	public List<String> retrieveMessage(String topic, String groupName, String host) throws 
											IOException, InterruptedException, Exception;

}
