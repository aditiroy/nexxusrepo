package com.att.sales.nexxus.mylogin;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.dao.model.NxMpMyloginMapping;
import com.att.sales.nexxus.dao.model.NxProfiles;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.repository.NxMpMyloginMappingRepository;
import com.att.sales.nexxus.dao.repository.NxProfilesRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;

@ExtendWith(MockitoExtension.class)
public class NexxusMyloginServiceTest {
	

	@InjectMocks
	private NexxusMyloginService nexxusMyloginService;
	
	@Mock
	private NxProfilesRepository nxProfilesRepository;
	
	private List<NxProfiles> nxProfiles;
	
	@Mock
	private NxMpMyloginMappingRepository nxMpMyloginMappingRepository;
	
	@Mock
	private GetUsersForGroupWSHandler getUsersForGroupWSHandler;
	
	@Mock
	private NxUserRepository nxUserRepository;
	
	@BeforeEach
	public void init() {
		nxProfiles = new ArrayList<>();
		NxProfiles nxProfile = new NxProfiles();
		nxProfile.setProfileName("General User");
		NxUser nxUser = new NxUser();
		nxUser.setActive("Y");
		nxUser.setUserAttId("abcde");
		nxUser.setFirstName("xyz");
		nxUser.setApprovalDate(new Date());
		List<NxUser> nxUsers = new ArrayList<>();
		nxUsers.add(nxUser);
		nxProfile.setNxUsers(nxUsers);
		nxProfiles.add(nxProfile);
		ReflectionTestUtils.setField(nexxusMyloginService, "myloginDocFooter","TotalRecords,replace_count,\"UADM USER DATA STANDARD 2.2\"");
		ReflectionTestUtils.setField(nexxusMyloginService, "myloginNexxusDocAllaccountFooter","TotalRecords,replace_count,30359");
		ReflectionTestUtils.setField(nexxusMyloginService, "nexxusAppname","Nexxus");
	}
	
	@Test
	public void testGetUserProfile() {
		Mockito.when(nxProfilesRepository.findAll()).thenReturn(nxProfiles);
		Mockito.when(nxUserRepository.findByNxProfiles(any())).thenReturn(nxProfiles.get(0).getNxUsers());
		List<NxMpMyloginMapping> userMappings = new ArrayList<>();
		NxMpMyloginMapping mapping = new NxMpMyloginMapping();
		mapping.setHeaderName("login");
		mapping.setVariableName("login");
		userMappings.add(mapping);
		Mockito.when(nxMpMyloginMappingRepository.findByProfileName(anyString(), anyString())).thenReturn(userMappings);
		doNothing().when(getUsersForGroupWSHandler).prepareDocumentData(any(), anyMap(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList());
		doNothing().when(getUsersForGroupWSHandler).createFile(anyList(), anyString(), anyString(), anyList());
		doNothing().when(getUsersForGroupWSHandler).fileUpload(anyList());
		nexxusMyloginService.getUserProfile();
		
	}

}
