package com.att.sales.nexxus.userdetails.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.model.NxAdminUserModel;
import com.att.sales.nexxus.dao.model.NxProfiles;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.NxUserFeatureMapping;
import com.att.sales.nexxus.dao.model.NxUserLoginDetails;
import com.att.sales.nexxus.dao.repository.NxAdminUserRepository;
import com.att.sales.nexxus.dao.repository.NxProfilesRepository;
import com.att.sales.nexxus.dao.repository.NxUserFeatureMappingRepository;
import com.att.sales.nexxus.dao.repository.NxUserLoginDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.userdetails.model.AddUserRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessRequest;
import com.att.sales.nexxus.userdetails.model.CheckAccessResponse;
import com.att.sales.nexxus.userdetails.model.FeatureDetails;

@Service
public class UserServiceImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private static final String REQUEST_TYPE_ADD = "Add";
	private static final String REQUEST_TYPE_CHANGE = "Change";
	private static final String REQUEST_TYPE_DELETE = "Delete";
	private static final String ERROR_MISSING_ATTUID = "AU0000";
	private static final String ERROR_INVALID_REQUEST_TYPE = "AU0001";
	private static final String ERROR_INVALID_APPROVAL_DATE = "AU0002";
	private static final String ERROR_INVALID_APPLICATION_PROFILE = "AU0003";
	private static final String ERROR_USER_EXISTS = "AU0004";
	private static final String ERROR_USER_DOES_NOT_EXIST = "AU0005";
	public static final String NONE = "NONE";
	private static final String SUCCESS = "Success";
	private static final String FAILED = "Failed";
	private SimpleDateFormat df = new SimpleDateFormat("M/dd/yyyy h:m:s a");

	@Autowired
	private NxProfilesRepository nxProfilesRepository;

	@Autowired
	private NxUserRepository nxUserRepository;

	@Autowired
	private NxUserLoginDetailsRepository nxUserLoginDetailsRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxAdminUserRepository nxAdminUserRepository;
	
	@Autowired
	private NxUserFeatureMappingRepository nxUserFeatureMappingRepository;

	public ServiceResponse addUser(AddUserRequest request) throws SalesBusinessException {
		logger.info("enter addUser method");
		validateAddUserRequest(request);
		ServiceResponse response = new ServiceResponse();

		NxProfiles nxProfiles = nxProfilesRepository.findByProfileNameAndActive(request.getApplicationProfile(), "Y");
		if (nxProfiles == null && !REQUEST_TYPE_DELETE.equalsIgnoreCase(request.getRequestType())) {
			addUserAslaLogging(request, FAILED);
			throw new SalesBusinessException(ERROR_INVALID_APPLICATION_PROFILE);
		}
		NxUser nxUser = nxUserRepository.findByUserAttId(request.getAttuid());
		if (REQUEST_TYPE_ADD.equalsIgnoreCase(request.getRequestType())) {
			if (nxUser != null && "Y".equalsIgnoreCase(nxUser.getActive())) {
				addUserAslaLogging(request, FAILED);
				throw new SalesBusinessException(ERROR_USER_EXISTS);
			}
			if (nxUser == null) {
				nxUser = new NxUser();
			}
			updateUserandAdminUser(request, nxUser, nxProfiles);
		} else if (REQUEST_TYPE_CHANGE.equalsIgnoreCase(request.getRequestType())) {
			if (nxUser == null || !"Y".equalsIgnoreCase(nxUser.getActive())) {
				addUserAslaLogging(request, FAILED);
				throw new SalesBusinessException(ERROR_USER_DOES_NOT_EXIST);
			}
			updateUserandAdminUser(request, nxUser, nxProfiles);
		} else if (REQUEST_TYPE_DELETE.equalsIgnoreCase(request.getRequestType())) {
			if (nxUser == null || !"Y".equalsIgnoreCase(nxUser.getActive())) {
				addUserAslaLogging(request, FAILED);
				throw new SalesBusinessException(ERROR_USER_DOES_NOT_EXIST);
			}
			List<NxAdminUserModel> nxAdminUsers = nxAdminUserRepository.findByAttuidOnly(request.getAttuid());
			if (!nxAdminUsers.isEmpty()) {
				NxAdminUserModel nxAdminUserModel = nxAdminUsers.get(0);
				nxAdminUserModel.setActiveYn("N");
				nxAdminUserRepository.save(nxAdminUserModel);
			}
			nxUser.setActive("N");
			nxUser.setApprovalDate(request.getApprovalDate_1());
			nxUserRepository.save(nxUser);
		}
		addUserAslaLogging(request, SUCCESS);
		logger.info("exit addUser method");
		return setSuccessResponse(response);
	}

	protected void addUserAslaLogging(AddUserRequest request, String status) {
		NxUserLoginDetails nxUserLoginDetails = new NxUserLoginDetails(request);
		nxUserLoginDetails.setStatus(status);
		nxUserLoginDetailsRepository.save(nxUserLoginDetails);
	}

	protected void updateUserandAdminUser(AddUserRequest request, NxUser nxUser, NxProfiles nxProfiles) {
		nxUser.setUserAttId(request.getAttuid());
		nxUser.setActive("Y");
//		nxUser.setApplicationUserId(null); // applicationUserId value?
		nxUser.setFirstName(request.getFirstName());
//		nxUser.setMiddleInitial(null); // no middleInitial in the request
		nxUser.setLastName(request.getLastName());
		nxUser.setEmail(request.getEmail());
		nxUser.setRequestorAttuid(request.getRequestorAttuid());
		nxUser.setNxProfiles(nxProfiles);
		nxUser.setAddress(request.getAddress1());
		nxUser.setState(request.getState());
		nxUser.setCity(request.getCity());
		nxUser.setPostalCode(request.getPostalCode());
		nxUser.setResourceType(request.getResourceType());
		nxUser.setPhone(request.getPhone());
		nxUser.setManagerAttuid(request.getManagerAttuid());
		nxUser.setApproverAttuid(request.getApproverAttuid());
		nxUser.setApprovalDate(request.getApprovalDate_1());
		nxUserRepository.save(nxUser);
		
		List<NxAdminUserModel> nxAdminUsers = nxAdminUserRepository.findByAttuidOnly(request.getAttuid());
		if ("Admin Access".equalsIgnoreCase(request.getApplicationProfile())) {
			NxAdminUserModel nxAdminUserModel = null;
			if (nxAdminUsers.isEmpty()) {
				nxAdminUserModel = new NxAdminUserModel();
			} else {
				nxAdminUserModel = nxAdminUsers.get(0);
			}
			nxAdminUserModel.setAttuid(request.getAttuid());
			nxAdminUserModel.setFname(request.getFirstName());
			nxAdminUserModel.setLname(request.getLastName());
			nxAdminUserModel.setPhone(request.getPhone());
			nxAdminUserModel.setEmail(request.getEmail());
			nxAdminUserModel.setActiveYn("Y");
			nxAdminUserRepository.save(nxAdminUserModel);
		} else if (!nxAdminUsers.isEmpty()) {
			NxAdminUserModel nxAdminUserModel = nxAdminUsers.get(0);
			nxAdminUserModel.setActiveYn("N");
			nxAdminUserRepository.save(nxAdminUserModel);
		}
	}

	protected void validateAddUserRequest(AddUserRequest request) throws SalesBusinessException {
		List<String> messageCodes = new ArrayList<>();
		if (request.getAttuid() == null || request.getAttuid().isEmpty()) {
			messageCodes.add(ERROR_MISSING_ATTUID);
		}
		if (!(REQUEST_TYPE_ADD.equalsIgnoreCase(request.getRequestType())
				|| REQUEST_TYPE_CHANGE.equalsIgnoreCase(request.getRequestType())
				|| REQUEST_TYPE_DELETE.equalsIgnoreCase(request.getRequestType()))) {
			messageCodes.add(ERROR_INVALID_REQUEST_TYPE);
		}
		try {
			Date approvalDate = df.parse(request.getApprovalDate());
			request.setApprovalDate_1(approvalDate);
		} catch (ParseException | NullPointerException e) {
			messageCodes.add(ERROR_INVALID_APPROVAL_DATE);
		}
		if (!messageCodes.isEmpty()) {
			throw new SalesBusinessException(messageCodes);
		}
	}

	public ServiceResponse checkAccess(CheckAccessRequest request) throws SalesBusinessException {
		
		if (request.getAttuid() != null && !request.getAttuid().isEmpty()) {
			request.setAttuid(request.getAttuid().toLowerCase());
		}
		logger.info("enter checkAccess method");
		validateCheckAccessRequest(request);

		CheckAccessResponse response = new CheckAccessResponse();
		response.setApplicationProfile(NONE);
		
		Map<String, String> property = nxMyPriceRepositoryServce.getDescDataFromLookup("NX_USER_PROPERTY");
		if ("Y".equals(property.get("ACCESS_CHECK_NX_ADMIN_USER_TABLE"))) {
			List<NxAdminUserModel> nxAdminUsers = nxAdminUserRepository.findByAttUid(request.getAttuid());
			if (nxAdminUsers.isEmpty()) {
				response.setApplicationProfile("General Access");
			} else {
				response.setApplicationProfile("Admin Access");
			}
			NxUserLoginDetails nxUserLoginDetails = new NxUserLoginDetails(request);
			if (NONE.equals(response.getApplicationProfile())) {
				nxUserLoginDetails.setStatus(FAILED);
			} else {
				nxUserLoginDetails.setStatus(SUCCESS);
			}
			nxUserLoginDetailsRepository.save(nxUserLoginDetails);
			
			
		} else {
			response.setApplicationProfile(getUserProfileName(request.getAttuid()));
			NxUserLoginDetails nxUserLoginDetails = new NxUserLoginDetails(request);
			if (NONE.equals(response.getApplicationProfile())) {
				nxUserLoginDetails.setStatus(FAILED);
			} else {
				nxUserLoginDetails.setStatus(SUCCESS);
			}
			nxUserLoginDetailsRepository.save(nxUserLoginDetails);
			
		}
		List<NxUserFeatureMapping> nxUserFeatureMapping= nxUserFeatureMappingRepository.findByNxUser_UserAttIdAndEnabled(request.getAttuid(),"Y");
		if(null!=nxUserFeatureMapping) {
			List<FeatureDetails> features= new ArrayList<>();
			for( NxUserFeatureMapping feature:nxUserFeatureMapping ) {
				FeatureDetails featureDetails=new FeatureDetails(feature.getNxFeature().getFeatureId(),feature.getNxFeature().getFeatureName());
				features.add(featureDetails);
			}
			response.setFeatures(features);
		}

		logger.info("exit checkAccess method");
		return setSuccessResponse(response);
	}
	
	public String getUserProfileName(String attuid) {
		String res = NONE;
		/*NxUser nxUser = nxUserRepository.findOne(attuid);
		if (nxUser != null && "Y".equals(nxUser.getActive()) && nxUser.getNxProfiles() != null
				&& nxUser.getNxProfiles().getProfileName() != null
				&& "Y".equals(nxUser.getNxProfiles().getActive())) {
			res = nxUser.getNxProfiles().getProfileName();
		}*/
		List<String> userResultList=nxUserRepository.findProfileNameByAttID(attuid);
		if(CollectionUtils.isNotEmpty(userResultList)) {
			res=userResultList.get(0);
		}
		return res;
	}

	protected void validateCheckAccessRequest(CheckAccessRequest request) throws SalesBusinessException {
		List<String> messageCodes = new ArrayList<>();
		if (request.getAttuid() == null || request.getAttuid().isEmpty()) {
			messageCodes.add(ERROR_MISSING_ATTUID);
		}
		if (!messageCodes.isEmpty()) {
			throw new SalesBusinessException(messageCodes);
		}
	}
}
