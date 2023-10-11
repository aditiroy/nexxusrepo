package com.att.sales.nexxus.mylogin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpMyloginMapping;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpMyloginMappingRepository;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSClientException;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.groups.GetGroupRequestType;
import com.oracle.xmlns.cpqcloud.groups.GetGroupResponseType;
import com.oracle.xmlns.cpqcloud.groups.GetGroups;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsRequestType;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsResponse;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsStatusType;
import com.oracle.xmlns.cpqcloud.groups.GroupUserType;
import com.oracle.xmlns.cpqcloud.groups.ObjectFactory;


@Component
public class GetUsersForGroupWSHandler {

	private static final Logger log = LoggerFactory.getLogger(GetUsersForGroupWSHandler.class);
	
	@Autowired
	@Qualifier("getUsersForGroupWSClientUtility")
	private SoapWSHandler getUsersWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;
	
	@Autowired
	private RestClientUtil restClientUtil;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMpMyloginMappingRepository nxMpMyloginMappingRepository;
	
	@Value("${mylogin.doc.footer}")
	private String myloginDocFooter;
	
	@Value("${mylogin.myprice.doc.allaccount.footer}")
	private String myloginMypriceDocAllaccountFooter;
	
	@Value("${pv.directory.nexxusoutput}")
	private String pvDirectoryNexxusoutput;	
	
	@Value("${mylogin.myprice.appname}")
	private String mypriceAppname;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@Autowired
	private MyloginService myloginService;
	
	@Autowired
	private HttpRestClient httpRestClient;

	
	public Boolean getUsersWebService(Map<String, Object> requestMap) throws  SalesBusinessException{
		log.info("Inside execute getUsersWebService to get users");
		Long currentTime = System.currentTimeMillis();
		Long startTime=System.currentTimeMillis() - currentTime;
		Boolean isSuccessful=false;
		try {
			ObjectFactory objectFactory = new ObjectFactory();
			GetGroupsResponse getGroupsResponse = null;
			GetGroups getGroupRequest = objectFactory.createGetGroups();
			List<NxLookupData> nxLookupDatas = nxLookupDataRepository.findByDatasetName(CommonConstants.MP_MYLOGIN_USER_GROUP_DS_NAME);
			this.prepareRequestBody(requestMap, getGroupRequest, objectFactory, nxLookupDatas);
			getUsersWSClientUtility.setWsName(MyPriceConstants.GET_USERS_WS);
			getGroupsResponse = wsProcessingService.initiateWebService(getGroupRequest,
					getUsersWSClientUtility, requestMap, GetGroupsResponse.class);
			if(null!=getGroupsResponse) {
				GetGroupsStatusType commonstatus = getGroupsResponse.getStatus();
				if(null!=commonstatus && commonstatus.getSuccess().equals("true")) {
					isSuccessful=true;
					processConfigResponse(getGroupsResponse, nxLookupDatas);
				}
			}	
		}catch(WSClientException  we) {
			isSuccessful=false;
			log.error("Exception during get users call: {}", we.getFaultString());
			requestMap.put(MyPriceConstants.RESPONSE_DATA, we.getFaultString());
		} catch(Exception  e) {
			isSuccessful=false;
			log.error("Exception during get users call: {}", e);
			requestMap.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
		}
		Long endTime=System.currentTimeMillis() - currentTime;
		log.info("End : getUsersWebService {}", (endTime - startTime));
		return isSuccessful;
	}
	
	protected void prepareRequestBody(Map<String, Object> requestMap, GetGroups groupRequest, ObjectFactory objectFactory, List<NxLookupData> nxLookupDatas) {
		
		List<GetGroupRequestType> groups = new ArrayList<GetGroupRequestType>();
		for(NxLookupData nxLookupData : nxLookupDatas) {
			GetGroupRequestType getGroupRequestType = objectFactory.createGetGroupRequestType();
			getGroupRequestType.setVariableName(nxLookupData.getItemId());
			groups.add(getGroupRequestType);
		}
		GetGroupsRequestType getGroupsRequestType = objectFactory.createGetGroupsRequestType();
		getGroupsRequestType.getGroup().addAll(groups);
		groupRequest.setGroups(getGroupsRequestType);
	}
	

	public void processConfigResponse(GetGroupsResponse getGroupsResponse, List<NxLookupData> nxLookupDatas) {
		if(null != getGroupsResponse 
				&& null != getGroupsResponse.getGroups() 
				&& null != getGroupsResponse.getGroups().getGroup()
				&& 0 != getGroupsResponse.getGroups().getGroup().size()) {
			List<String> userContent = new ArrayList<String>();
			List<String> allAccountContent = new ArrayList<String>();
			List<String> userProfileContent = new ArrayList<String>();
			List<String> profileContent = new ArrayList<String>();
			List<String> fileNames = new ArrayList<String>();
			try {
				log.info("getUsersWebService  app name {}",mypriceAppname);
				Map<String, List<String>> userProfile = new HashMap<String, List<String>>();
				List<NxMpMyloginMapping> userMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.USER, mypriceAppname);
				String header = userMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				userContent.add(header);
				List<NxMpMyloginMapping> allAccountsMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.ALLACCOUNTS, mypriceAppname);
				String allAccountHeader = allAccountsMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				allAccountContent.add(allAccountHeader);
				List<NxMpMyloginMapping> userProfileMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.USERPROFILES, mypriceAppname);
				String userProfileheader = userProfileMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				userProfileContent.add(userProfileheader);
				List<NxMpMyloginMapping> profileMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.PROFILE, mypriceAppname);
				String profileHeader = profileMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				profileContent.add(profileHeader);
				for(GetGroupResponseType resp: getGroupsResponse.getGroups().getGroup()) {
					if(resp.getUsers() != null && resp.getUsers().getUser() != null) {
						for(GroupUserType user : resp.getUsers().getUser()) {
							Map<String, Object> result = callGetUsers(user.getLogin());
							//int code = (int) result.get(MyPriceConstants.RESPONSE_CODE);
						//	if (code == CommonConstants.SUCCESS_CODE) {
								if (result.get(MyPriceConstants.RESPONSE_DATA) != null) {
									UserDetails userDetails = (UserDetails) restClientUtil.processResult((String) result.get(MyPriceConstants.RESPONSE_DATA),
											UserDetails.class);
									if(userDetails.getExternalSsoId() != null) {
										userDetails.setProfileName(getProfileName(resp.getVariableName(), nxLookupDatas));
										if("1".equalsIgnoreCase(userDetails.getStatus())) {
											userDetails.setStatus(CommonConstants.MYLOGIN_STATUS_ACTIVE);
										}else {
											userDetails.setStatus(CommonConstants.MYLOGIN_STATUS_INACTIVE);
										}
										prepareDocumentData(userDetails, userProfile, userMappings, allAccountsMappings, userProfileMappings, profileMappings,
											userContent, allAccountContent, userProfileContent, profileContent);
									}
								}
							//}
						}
					}
				}
				String footer = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((userContent.size()+1)));
				userContent.add(footer);
				createFile(userContent, mypriceAppname, CommonConstants.USER, fileNames);
				
				String userProfilefooter = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((userProfileContent.size()+1)));
				userProfileContent.add(userProfilefooter);
				createFile(userProfileContent, mypriceAppname, CommonConstants.USERPROFILES, fileNames);
				
				String profileContentfooter = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((profileContent.size()+1)));
				profileContent.add(profileContentfooter);
				createFile(profileContent, mypriceAppname, CommonConstants.PROFILE, fileNames);
				
				String allAccountFooter = myloginMypriceDocAllaccountFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((allAccountContent.size()-1)));
				allAccountContent.add(allAccountFooter);
				createFile(allAccountContent, mypriceAppname, CommonConstants.ALLACCOUNTS, fileNames);
				log.info("document created successfully");
				
				fileUpload(fileNames);
				log.info("document uploaded successfully");
				fileNames = null;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void fileUpload(List<String> fileNames) {
		try {
			myloginService.connect();
			for(String fileName : fileNames) {
				myloginService.upload(fileName);
			}
			myloginService.disconnect();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getProfileName(String profileName, List<NxLookupData> nxLookupDatas) {
		return nxLookupDatas.stream().filter(l -> l.getItemId().equalsIgnoreCase(profileName)).findFirst().get().getDescription();
	}
	
	public void createFile(List<String> data, String appName, String profileName, List<String> fileNames) {
		File dir = new File(pvDirectoryNexxusoutput);  //NOSONAR
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyyMMdd");
		String stringDate= DateFor.format(new Date());
		String fileName = appName+"_"+profileName+"_"+stringDate+".txt";
		Path path = Paths.get(pvDirectoryNexxusoutput+fileName);
	     try {
			Files.write(path, data);
			fileNames.add(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Object> callGetUsers(String userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String response = httpRestClient.callHttpRestClient(env.getProperty("myprice.getUser").replace("{loginId}", userId), HttpMethod.GET, null, null, 
					headers, proxy);
			
			result.put(MyPriceConstants.RESPONSE_DATA, response);
			
		} catch (SalesBusinessException e) {
			log.error("Exception occured while calling get users for mylogin {}", e);
		}
		return result;
		
	}
	
	void prepareDocumentData(UserDetails userDetails, Map<String, List<String>> userProfile, List<NxMpMyloginMapping> userMappings, 
			List<NxMpMyloginMapping> allAccountMappings, List<NxMpMyloginMapping> userProfileMappings, List<NxMpMyloginMapping> profileMappings,
			List<String> userContent, List<String> allAccountContent, List<String> userProfileContent, List<String> profileContent) {
		try {
			boolean user = false, profile = false, userProf = false, allAccount = false;
			if(!userProfile.containsKey(userDetails.getProfileName())) {
				profile = true;
				userProfile.put(userDetails.getProfileName(), new ArrayList<String>());
			}
			// check if user exist in other profile 
			boolean isUserExist = false;
			for (String key: userProfile.keySet()) {
				if(userProfile.get(key).contains(userDetails.getLogin())) {
					isUserExist = true;
					break;
				}
			}
			if(isUserExist) {
				if(!userProfile.get(userDetails.getProfileName()).contains(userDetails.getLogin())) {
					userProf = true;
					allAccount = true;
					userProfile.get(userDetails.getProfileName()).add(userDetails.getLogin());
				}
			}else {
				user = true; userProf = true; allAccount = true;
				userProfile.get(userDetails.getProfileName()).add(userDetails.getLogin());
			}
		
				
			if(user && CommonConstants.MYLOGIN_STATUS_ACTIVE.equalsIgnoreCase(userDetails.getStatus())) {
				StringBuffer userData = new StringBuffer();
				for(NxMpMyloginMapping nxMpMyloginMapping : userMappings) {
					if(nxMpMyloginMapping.getVariableName() != null) {
						processData(nxMpMyloginMapping, userDetails, userData);
					}else if(nxMpMyloginMapping.getConstantValue() != null){
						userData.append(nxMpMyloginMapping.getConstantValue()).append(",");
					}
					else {
						userData.append(",");
					}
				}
				if(userData.length() > 0) {
					userContent.add(userData.toString().substring(0, userData.length()-1));
				}
				userData = null;
			}
		
			// all account 
			if(allAccount && CommonConstants.MYLOGIN_STATUS_ACTIVE.equalsIgnoreCase(userDetails.getStatus())) {
				StringBuffer accountData = new StringBuffer();
				for(NxMpMyloginMapping nxMpMyloginMapping : allAccountMappings) {
					if(nxMpMyloginMapping.getVariableName() != null) {
						processData(nxMpMyloginMapping, userDetails, accountData);
					}else if(nxMpMyloginMapping.getConstantValue() != null){
						accountData.append(nxMpMyloginMapping.getConstantValue()).append(",");
					}else {
						accountData.append(",");
					}
				}
				if(accountData.length() > 0) {
					allAccountContent.add(accountData.toString().substring(0, accountData.length()-1));
				}
				accountData = null;
			}
			
			// userprofile
			if(userProf && CommonConstants.MYLOGIN_STATUS_ACTIVE.equalsIgnoreCase(userDetails.getStatus())) {
				StringBuffer userProfileData = new StringBuffer();
				for(NxMpMyloginMapping nxMpMyloginMapping : userProfileMappings) {
					if(nxMpMyloginMapping.getVariableName() != null) {
						processData(nxMpMyloginMapping, userDetails, userProfileData);
					}else if(nxMpMyloginMapping.getConstantValue() != null){
						userProfileData.append(nxMpMyloginMapping.getConstantValue()).append(",");
					}else {
						userProfileData.append(",");
					}
				}
				if(userProfileData.length() > 0) {
					userProfileContent.add(userProfileData.toString().substring(0, userProfileData.length()-1));
				}
				userProfileData = null;
			}
			
			// profile
			if(profile) {
				StringBuffer profileData = new StringBuffer();
				for(NxMpMyloginMapping nxMpMyloginMapping : profileMappings) {
					if(nxMpMyloginMapping.getVariableName() != null) {
						processData(nxMpMyloginMapping, userDetails, profileData);
					}else if(nxMpMyloginMapping.getConstantValue() != null){
						profileData.append(nxMpMyloginMapping.getConstantValue()).append(",");
					}else {
						profileData.append(",");
					}
				}
				if(profileData.length() > 0) {
					profileContent.add(profileData.toString().substring(0, profileData.length()-1));
				}
				profileData = null;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception while preparing document content");
		}
	}
	
	public void processData(NxMpMyloginMapping nxMpMyloginMapping, UserDetails userDetails, StringBuffer userData) {
		try {
			Field field = UserDetails.class.getDeclaredField(nxMpMyloginMapping.getVariableName());
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object obj = field.get(userDetails);
			field.setAccessible(accessible);
			if(obj != null) {
				String data = obj.toString();
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(nxMpMyloginMapping.getQuote())) {
					data = "\""+data+"\"";//StringUtils.rightPad(StringUtils.leftPad(data, 1, "\""), 1, "\"");
				}
				userData.append(data).append(",");
				
			}else {
				userData.append(",");
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			e.getCause().toString();
		}
		
	}
}
