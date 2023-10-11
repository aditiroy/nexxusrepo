package com.att.sales.nexxus.mylogin;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpMyloginMapping;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpMyloginMappingRepository;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.att.sales.nexxus.ws.utility.GetUsersForGroupWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSClientException;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.oracle.xmlns.cpqcloud.groups.GetGroupRequestType;
import com.oracle.xmlns.cpqcloud.groups.GetGroupResponseType;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsResponse;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsResponseType;
import com.oracle.xmlns.cpqcloud.groups.GetGroupsStatusType;
import com.oracle.xmlns.cpqcloud.groups.GroupUserType;
import com.oracle.xmlns.cpqcloud.groups.UsersResponseType;

@ExtendWith(MockitoExtension.class)
public class GetUsersForGroupWSHandlerTest {
	
	
	@InjectMocks
	GetUsersForGroupWSHandler getUsersForGroupWSHandler;
	
	private Map<String, Object> requestMap;
	
	private List<NxLookupData> nxLookupDatas;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private WSProcessingService wsProcessingService;
	
	@Mock
	private GetUsersForGroupWSClientUtility getUsersForGroupWSClientUtility;
	
	@Mock
	private NxMpMyloginMappingRepository nxMpMyloginMappingRepository;
	
	@Mock
	private Map<String, String> headers;
	
	@Mock
	private RestClientUtil restClientUtil;
	
	@Mock
	private Environment env;
	
	@Mock
	private Path path;
	
	@Mock
	private MyloginService myloginService;
	
	@Mock
	private HttpRestClient httpRest;
	
	@BeforeEach
	public void init() {
		requestMap = new HashMap<String, Object>();
		nxLookupDatas = new ArrayList<NxLookupData>();
		NxLookupData l1 = new NxLookupData();
		l1.setItemId("pricingManager");
		l1.setDescription("Pricer Retail");
		nxLookupDatas.add(l1);
		
		ReflectionTestUtils.setField(getUsersForGroupWSHandler, "myloginDocFooter","TotalRecords,replace_count,\"UADM USER DATA STANDARD 2.2\"");
		ReflectionTestUtils.setField(getUsersForGroupWSHandler, "myloginMypriceDocAllaccountFooter","TotalRecords,replace_count,27226");
		ReflectionTestUtils.setField(getUsersForGroupWSHandler, "mypriceAppname","MyPrice");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetUsersWebService() throws SalesBusinessException {
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupDatas);
		Mockito.when(wsProcessingService.initiateWebService(any(GetGroupRequestType.class),
				any(GetUsersForGroupWSClientUtility.class), anyMap(), any(Class.class)))
				.thenReturn(getUserResponse());
		doNothing().when(getUsersForGroupWSClientUtility).setWsName(anyString());
		List<NxMpMyloginMapping> userMappings = new ArrayList<>();
		NxMpMyloginMapping mapping = new NxMpMyloginMapping();
		mapping.setHeaderName("login");
		mapping.setVariableName("login");
		userMappings.add(mapping);
		Mockito.when(nxMpMyloginMappingRepository.findByProfileName(anyString(), anyString())).thenReturn(userMappings);
		String result = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(result);
		Mockito.when(env.getProperty(anyString())).thenReturn("username");
		UserDetails userDetails = new UserDetails();
		userDetails.setLogin("abcdf");
		userDetails.setProfileName("pricingManager");
		userDetails.setFirstName("xyz");
		Mockito.when(restClientUtil.processResult(anyString(), any())).thenReturn(userDetails);
		getUsersForGroupWSHandler = spy(getUsersForGroupWSHandler);
		doNothing().when(getUsersForGroupWSHandler).createFile(anyList(), anyString(), anyString(), anyList());
		
		try {
			doNothing().when(myloginService).connect();
			doNothing().when(myloginService).upload(anyString());
			doNothing().when(myloginService).disconnect();
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
		}
		
		
		getUsersForGroupWSHandler.getUsersWebService(requestMap);
	}
	
	private GetGroupsResponse getUserResponse() {
		GetGroupsResponse response = new GetGroupsResponse();
		GetGroupsResponseType groupsResType = new GetGroupsResponseType();
		GetGroupResponseType groupResType = new GetGroupResponseType();
		groupResType.setVariableName("pricingManager");
		UsersResponseType userResType = new UsersResponseType();
		GroupUserType groupUserType = new GroupUserType();
		groupUserType.setLogin("abcdf");
		userResType.getUser().add(groupUserType);
		groupResType.setUsers(userResType);
		groupsResType.getGroup().add(groupResType);
		response.setGroups(groupsResType);
		GetGroupsStatusType commonstatus = new GetGroupsStatusType();
		commonstatus.setSuccess("true");
		response.setStatus(commonstatus);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetUsersWebServiceWSExc() throws SalesBusinessException {
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupDatas);
		Mockito.when(wsProcessingService.initiateWebService(any(GetGroupRequestType.class),
				any(GetUsersForGroupWSClientUtility.class), anyMap(), any(Class.class)))
				.thenThrow(new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR, "fault code", "faultString", null));
		doNothing().when(getUsersForGroupWSClientUtility).setWsName(anyString());
		
		getUsersForGroupWSHandler.getUsersWebService(requestMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetUsersWebServiceExc() throws SalesBusinessException {
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupDatas);
		Mockito.when(wsProcessingService.initiateWebService(any(GetGroupRequestType.class),
				any(GetUsersForGroupWSClientUtility.class), anyMap(), any(Class.class)))
				.thenThrow(new NullPointerException());
		doNothing().when(getUsersForGroupWSClientUtility).setWsName(anyString());
		
		getUsersForGroupWSHandler.getUsersWebService(requestMap);
	}

}
