package com.att.sales.nexxus.mylogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpMyloginMapping;
import com.att.sales.nexxus.dao.model.NxProfiles;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.repository.NxMpMyloginMappingRepository;
import com.att.sales.nexxus.dao.repository.NxProfilesRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;

@Component
public class NexxusMyloginService {
	
	private static final Logger log = LoggerFactory.getLogger(NexxusMyloginService.class);
	
	@Autowired
	private GetUsersForGroupWSHandler getUsersForGroupWSHandler;
	
	@Autowired
	private NxMpMyloginMappingRepository nxMpMyloginMappingRepository;
	
	@Value("${mylogin.doc.footer}")
	private String myloginDocFooter;
	
	@Value("${mylogin.nexxus.doc.allaccount.footer}")
	private String myloginNexxusDocAllaccountFooter;
	
	@Value("${mylogin.nexxus.appname}")
	private String nexxusAppname;
	
	@Autowired
	private NxProfilesRepository nxProfilesRepository;
	
	@Autowired
	private NxUserRepository nxUserRepository;
	
	public void getUserProfile() {
		List<NxProfiles> nxProfiles = nxProfilesRepository.findAll();
		processResponse(nxProfiles);
	}
	
	private void processResponse(List<NxProfiles> nxProfiles) {
		if(CollectionUtils.isNotEmpty(nxProfiles)) {
			List<String> userContent = new ArrayList<String>();
			List<String> allAccountContent = new ArrayList<String>();
			List<String> userProfileContent = new ArrayList<String>();
			List<String> profileContent = new ArrayList<String>();
			List<String> fileNames = new ArrayList<String>();
			log.info("NexxusMyloginService  app name {}",nexxusAppname);
			try {
				Map<String, List<String>> userProfile = new HashMap<String, List<String>>();
				List<NxMpMyloginMapping> userMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.USER, nexxusAppname);
				String header = userMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				userContent.add(header);
				List<NxMpMyloginMapping> allAccountsMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.ALLACCOUNTS, nexxusAppname);
				String allAccountHeader = allAccountsMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				allAccountContent.add(allAccountHeader);
				List<NxMpMyloginMapping> userProfileMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.USERPROFILES, nexxusAppname);
				String userProfileheader = userProfileMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				userProfileContent.add(userProfileheader);
				List<NxMpMyloginMapping> profileMappings = nxMpMyloginMappingRepository.findByProfileName(CommonConstants.PROFILE, nexxusAppname);
				String profileHeader = profileMappings.stream().map(h -> h.getHeaderName()).collect(Collectors.joining(","));
				profileContent.add(profileHeader);
				for(NxProfiles nxProfile : nxProfiles) {
					if(nxProfile != null) {
						List<NxUser> nxUsers = nxUserRepository.findByNxProfiles(nxProfile);
						if(CollectionUtils.isNotEmpty(nxUsers)) {
							for(NxUser user : nxUsers) {
								UserDetails userDetails = new UserDetails();
								userDetails.setLogin(user.getUserAttId());
								userDetails.setFirstName(user.getFirstName());
								userDetails.setMiddleInitial(user.getMiddleInitial());
								userDetails.setLastName(user.getLastName());
								userDetails.setEmail(user.getEmail());
								userDetails.setApprovalDate(user.getApprovalDate() != null ? user.getApprovalDate().toString() : null);
								userDetails.setLastLogonDate(user.getLastLogonDate());
								if(StringConstants.CONSTANT_Y.equalsIgnoreCase(user.getActive())) {
									userDetails.setStatus(CommonConstants.MYLOGIN_STATUS_ACTIVE);
								}else {
									userDetails.setStatus(CommonConstants.MYLOGIN_STATUS_INACTIVE);
								}
								userDetails.setProfileName(nxProfile.getProfileName());
								getUsersForGroupWSHandler.prepareDocumentData(userDetails, userProfile, userMappings, allAccountsMappings, userProfileMappings, profileMappings,
										userContent, allAccountContent, userProfileContent, profileContent);
							}
						}
					}
				}
				String footer = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((userContent.size()+1)));
				userContent.add(footer);
				getUsersForGroupWSHandler.createFile(userContent, nexxusAppname, CommonConstants.USER, fileNames);
				
				String userProfilefooter = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((userProfileContent.size()+1)));
				userProfileContent.add(userProfilefooter);
				getUsersForGroupWSHandler.createFile(userProfileContent, nexxusAppname, CommonConstants.USERPROFILES, fileNames);
				
				String profileContentfooter = myloginDocFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((profileContent.size()+1)));
				profileContent.add(profileContentfooter);
				getUsersForGroupWSHandler.createFile(profileContent, nexxusAppname, CommonConstants.PROFILE, fileNames);
				
				String allAccountFooter = myloginNexxusDocAllaccountFooter.replace(CommonConstants.REPLACE_COUNT, String.valueOf((allAccountContent.size()-1)));
				allAccountContent.add(allAccountFooter);
				getUsersForGroupWSHandler.createFile(allAccountContent, nexxusAppname, CommonConstants.ALLACCOUNTS, fileNames);
				log.info("document created successfully");
				
				getUsersForGroupWSHandler.fileUpload(fileNames);
				log.info("document uploaded successfully");
				fileNames = null;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
