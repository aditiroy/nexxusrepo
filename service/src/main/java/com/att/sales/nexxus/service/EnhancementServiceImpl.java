package com.att.sales.nexxus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.model.NewEnhancementResponse;


@Service
public class EnhancementServiceImpl extends BaseServiceImpl implements EnhancementService {

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	  public ServiceResponse fetchNewEnhancements(NewEnhancementRequest request) {
		  NewEnhancementResponse response = new NewEnhancementResponse();
		  List<NxLookupData> list = nxLookupDataRepository.findByDatasetNameAndActive(StringConstants.WHATS_NEW,StringConstants.Y);
		  response.setEnhancements( list.get(0).getCriteria());
		  setSuccessResponse(response);
		  return response;
	}
}
