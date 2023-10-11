/**
 * 
 */
package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.chatbot.model.ChatBotRequest;
import com.att.sales.nexxus.chatbot.model.ElizaIntents;
import com.att.sales.nexxus.chatbot.model.ElizaQuestions;
import com.att.sales.nexxus.chatbot.model.ElizaResponse;
import com.att.sales.nexxus.chatbot.model.ElizaSyncIntent;
import com.att.sales.nexxus.chatbot.model.ElizaSyncResponse;
import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author aa316k
 *
 */
@Service
public class ChatBotServiceImpl extends BaseServiceImpl implements ChatBotService  {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ChatBotServiceImpl.class);
	
	@Autowired
	private EntityManager em;
	
	@Value("${ai.authKeyy}")
	private String authKeyy;
	
	@Value("${ai.uri}")
	private String uri;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private HttpRestClient httpRestClient;
	

	public ServiceResponse datafeed(ChatBotRequest request) throws SalesBusinessException {
		ElizaResponse resp = new ElizaResponse();
		String actionType = request.getActionType();
		String input = request.getSearchString();
		
		if("IntentPrediction".equalsIgnoreCase(actionType)) {
			Map<String, Set<String>> intentMap = new HashMap<String,Set<String>>();
			List<String> intentResponseList = retrieveIntents(input);
			String sql = "select * from ELIZA_INTENT_DETAILS where intent = :intent";
			Query query = em.createNativeQuery(sql);
			query.setParameter("intent", intentResponseList.get(0));
			List<Object[]> intentList = query.getResultList();
			if(intentList != null && !intentList.isEmpty()) {
					for(Object[] intentEntry : intentList) {
						String output = intentEntry[0].toString();
						output = output.substring(0, output.length()-2);
						String intent = output;
						String displayQuestion = "";
						if(null != intentEntry[1] && !"".equals(intentEntry[1])) {
							displayQuestion = intentEntry[1].toString();
						}
						String intentKey = intent +"##"+ displayQuestion;
						String question = intentEntry[2].toString();
						String answer = intentEntry[3].toString();
						String intentValue = question+"##"+answer;
						Set<String>finalIntentValue = new HashSet<String>();
						if(intentMap.containsKey(intentKey)) {
							finalIntentValue = intentMap.get(intentKey);
						}
						finalIntentValue.add(intentValue);
						intentMap.put(intentKey, finalIntentValue);
					}
					
					List<ElizaIntents> intentOutputList = new ArrayList<ElizaIntents>();
					for(Map.Entry<String,Set<String>> entry: intentMap.entrySet()){
						ElizaIntents intents = new ElizaIntents();
						String intentKey = entry.getKey();
						Set<String> intentValue = entry.getValue();
						String[] intentKeyEntry = intentKey.split("##");
						intents.setIntent(intentKeyEntry[0]);
						String dispQuestion = "";
						if(intentKeyEntry.length > 1 && null != intentKeyEntry[1] && !"".equals(intentKeyEntry[1])) {
							dispQuestion = intentKeyEntry[1];
						}
						intents.setDisplayquestion(dispQuestion);
						List<ElizaQuestions> questionList = new ArrayList<ElizaQuestions>();
						for(String quesAnsEntry: intentValue) {
							ElizaQuestions questions = new ElizaQuestions();
							String[] quesAnsEntryList = quesAnsEntry.split("##");
							questions.setQuestion(quesAnsEntryList[0]);
							questions.setAnswer(quesAnsEntryList[1]);
							questionList.add(questions);
						}
						intents.setQuestions(questionList);
						intentOutputList.add(intents);
					}
					resp.setIntents(intentOutputList);
					logger.info("Processing done");
			}	
		}else if("Default".equalsIgnoreCase(actionType)) {
			resp.setChatBot("Hi, I am your virtual personal assistant to help you with nexxus related queries.Type your query below ");
			}

		return (ElizaResponse) setSuccessResponse(resp);
	}
	
	private List<String> retrieveIntents(String request) throws SalesBusinessException {
		List<String> intentResponseList = new ArrayList<String>();
		Map<String, String> headers  = new HashMap<String, String>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+authKeyy);
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "text/plain");
		String syncResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, request, 
				headers, null);
		syncResponse = syncResponse.replace("||", "");
		ElizaSyncResponse response = null;
		try {
			response = mapper.readValue(syncResponse, ElizaSyncResponse.class);
			int intentCount = 0;
			for(ElizaSyncIntent intents: response.getIntent_ranking()) {
				if(Double.parseDouble(intents.getConfidence()) > 0.25) {
					intentResponseList.add(intents.getName());
					intentCount++;
				}
				if(intentCount >=5) {
					break;
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return intentResponseList;
	}


}
