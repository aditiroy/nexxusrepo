package com.att.sales.nexxus.common;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * The listener interface for receiving kafkaTopic events.
 * The class that is interested in processing a kafkaTopic
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addKafkaTopicListener<code> method. When
 * the kafkaTopic event occurs, that object's appropriate
 * method is invoked.
 *
 * @see KafkaTopicEvent
 */
@Service
public class KafkaTopicListener {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(KafkaTopicListener.class);
	/*
	@KafkaListener(topics = "sales_topic",
		    groupId = "sales_test_lijo")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
		
		log.info("Message received...");
		log.info(cr.toString());
        
    }*/
}