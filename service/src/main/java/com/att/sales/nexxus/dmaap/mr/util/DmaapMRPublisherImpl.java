package com.att.sales.nexxus.dmaap.mr.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.msgrtr.referenceClient.MRBatchingPublisher;
import com.att.msgrtr.referenceClient.MRClientFactory;
import com.att.msgrtr.referenceClient.response.MRPublisherResponse;
import com.att.sales.nexxus.service.WebServiceErrorAlertService;

/**
 * The Class DmaapMRPublisherImpl.
 */
@Service
public class DmaapMRPublisherImpl implements IDmaapMRPublisher {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(DmaapMRPublisherImpl.class);

	/** The response code. */
	private String responseCode = null;

	/** The props. */
	private Properties props = new Properties();

	/** The mr producer file path. */
	@Value("${mr.producer.prop.path}")
	private String mrProducerFilePath;
	
	@Value("${mr.producer.secret}")
	private String dmaapSecret;
	
	@Autowired
	private WebServiceErrorAlertService webServiceErrorAlertService;

	/**
	 * Sets the mr producer file path.
	 *
	 * @param mrProducerFilePath the new mr producer file path
	 */
	public void setMrProducerFilePath(String mrProducerFilePath) {
		this.mrProducerFilePath = mrProducerFilePath;
	}

	/**
	 * Gets the response code.
	 *
	 * @return the response code
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * Sets the response code.
	 *
	 * @param responseCode the new response code
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.dmaap.mr.util.IDmaapMRPublisher#publishMessage(java.lang.String, java.lang.String)
	 */
	/*public String publishMessage(String topic, String jsonMessage) throws IOException {
		log.info("Inside publishMessage of DmaapMR.");
		FileReader reader=null;
		try {
			reader = new FileReader(mrProducerFilePath); //NOSONAR
			props.load(reader);
			props.setProperty("topic", topic);
			log.info("Before createBatchingPublisher.");
			final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher(props);
			log.info("After createBatchingPublisher.");

			pub.send("publisher message", jsonMessage);
			log.info("After sending message.");
			final List<message> stuck = pub.close(10, TimeUnit.SECONDS);
			if (!stuck.isEmpty()) {
				// response code is 400 if failed to publish due to any error
				log.error("{} messages unsent", stuck.size());
				this.setResponseCode("400");
			} else {
				log.info("Clean exit; all messages sent.");
				// response code is 200 if successfully published
				this.setResponseCode("200");
			}
		} catch (Exception e) {
			webServiceErrorAlertService.serviceErrorAlert(topic + "###" + jsonMessage, "dmaap", "PUB", null, null, e);
			log.error("Exception in publishing the message {} \n{}", e.getCause(), ExceptionUtils.getStackTrace(e));

		}finally{
			if(null!=reader){
				try {
					reader.close();
				} catch (IOException e) {
					log.info("Exception in closing FileReader {}", e.getMessage(), e);
				}
			}
		}
		return responseCode;
	}*/

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.dmaap.mr.util.IDmaapMRPublisher#publishMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String publishMessage(String topic, String group, String jsonMessage, String host) throws IOException {
		log.info("Inside publishMessage of DmaapMR.");
		FileReader reader=null;
		try {
			reader = new FileReader(mrProducerFilePath); //NOSONAR
			props.load(reader);
			props.setProperty("topic", topic);
			props.setProperty("group", group);
			props.setProperty("host", host);
			props.setProperty("password", dmaapSecret);
			log.info("Before createBatchingPublisher.");
			final MRBatchingPublisher pub = MRClientFactory.createBatchingPublisher(props);
			log.info("After createBatchingPublisher.");

			pub.send(jsonMessage);
			log.info("After sending message.");
			final MRPublisherResponse stuck = pub.retryAndCloseWithPossibleDuplication(10, TimeUnit.SECONDS);
			if (stuck.getPendingMsgs()>0) {
				// response code is 400 if failed to publish due to any error
				log.error("{} messages unsent", stuck.getPendingMsgs());
				this.setResponseCode("400");
			} else {
				log.info("Clean exit; all messages sent.");
				// response code is 200 if successfully published
				this.setResponseCode("200");
			}
		} catch (Exception e) {
			webServiceErrorAlertService.serviceErrorAlert(topic + "###" + group + "###" + jsonMessage, "dmaap", "PUB", null, null, e);
			log.error("Exception in publishing the message {} \n{}", e.getCause(), ExceptionUtils.getStackTrace(e));

		}finally{
			if(null!=reader){
				try {
					reader.close();
				} catch (IOException e) {
					log.info("Exception in closing FileReader {}", e.getMessage(), e);
				}
			}
		}
		return responseCode;
	}

}
