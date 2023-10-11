package  com.att.sales.nexxus.dmaap.mr.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.msgrtr.referenceClient.MRClientFactory;
import com.att.msgrtr.referenceClient.MRConsumer;
import com.att.sales.nexxus.service.WebServiceErrorAlertService;

/**
 * The Class DmaapMRSubscriberImpl.
 */
@Service
public class DmaapMRSubscriberImpl implements IDmaapMRSubscriber {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(DmaapMRSubscriberImpl.class);
	
	/** The props. */
	private Properties props = new Properties();

	/** The mr consumer prop file path. */
	@Value("${mr.consumer.prop.path}")
	private String mrConsumerPropFilePath;
  
  	@Value("${mr.consumer.secret}")
	private String dmaapSecret;

  	@Autowired
	private WebServiceErrorAlertService webServiceErrorAlertService;
	
	public List<String> retrieveMessage(String topic, String groupName, String host) throws Exception {
		//System.setProperty("DME2.DEBUG", "true");
		//System.setProperty("AFT_DME2_HTTP_EXCHANGE_TRACE_ON", "true");
		List<String> messagesList = new ArrayList<>();
		FileReader reader=null;
		try {
			reader = new FileReader(new File(mrConsumerPropFilePath)); //NOSONAR
			props.load(reader);
			props.setProperty("topic", topic);
			props.setProperty("group", groupName);
			props.setProperty("host", host);
            props.setProperty("password", dmaapSecret);
       
			//create consumer object from properties file
			final MRConsumer consumer = MRClientFactory.createConsumer(props);
			//fetch method of consumer object will retrieve any latest changes to existing topic
			for (String msg : consumer.fetch()) {				
				messagesList.add(msg);
				logger.info("dmaap message received from Ipne {}",msg);
			}
		}catch (Exception x) {
				webServiceErrorAlertService.serviceErrorAlert(topic + "###" + groupName, "dmaap", "SUB", null, null, x);
				logger.error("Exception while getting Dmaap Message Router messages: {}",x.getMessage());
				x.printStackTrace();
		}finally{
			if(null!=reader){
				try {
					reader.close();
				} catch (IOException e) {
					logger.info("Exception in closing FileReader {}", e.getMessage(), e);
				}

			}
		}
		return messagesList;
	}
}
