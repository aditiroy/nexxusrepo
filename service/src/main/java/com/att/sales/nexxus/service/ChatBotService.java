package com.att.sales.nexxus.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.chatbot.model.ChatBotRequest;
import com.att.sales.nexxus.chatbot.model.ElizaResponse;

public interface ChatBotService {
	
	
	public ServiceResponse datafeed(ChatBotRequest request) throws SalesBusinessException ;

}
