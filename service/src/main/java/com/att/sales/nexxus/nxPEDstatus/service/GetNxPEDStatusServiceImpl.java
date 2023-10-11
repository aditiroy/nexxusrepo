package com.att.sales.nexxus.nxPEDstatus.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusResponse;
import com.att.sales.nexxus.nxPEDstatus.model.Solution;
import com.att.sales.nexxus.nxPEDstatus.model.SolutionDetails;

@Service("getNxPEDStatusServiceImpl")
public class GetNxPEDStatusServiceImpl extends BaseServiceImpl implements GetNxPEDStatusService {

	private static Logger log = LoggerFactory.getLogger(GetNxPEDStatusServiceImpl.class);

	public static final String classNameEx = "GetNxPEDStatusServiceImpl >>>> ";
	public static final String getnXPEDStatusEx = "getnXPEDStatus >>>> ";

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxDesignRepository nxDesignRepository;

	@Override
	public ServiceResponse getnXPEDStatus(GetNxPEDStatusRequest request) {
		GetNxPEDStatusResponse response = new GetNxPEDStatusResponse();
		log.info(classNameEx + getnXPEDStatusEx + " Start : ");
		try {
			Long nxSolutionId = nxSolutionDetailsRepository.findSolutionByExternalKey(Long.parseLong(request.getSolutionId()));
			List<Object[]> designDataList = nxDesignRepository.findDesignIdsNbundleCdByNxSolutionId(nxSolutionId);
			List<Long> nxDesignIds = designDataList.stream().map(object->Long.parseLong(String.valueOf(object[0]))).collect(Collectors.toList());
			List<String> bundleCodes = designDataList.stream().distinct().map(object->String.valueOf(object[1])).collect(Collectors.toList());
			String bundleCode = null;
			if(CollectionUtils.isNotEmpty(bundleCodes)) {
				bundleCode = bundleCodes.get(0);
			}
			List<SolutionDetails> solutionDetailsList = read(nxDesignIds);
			Solution solution = new Solution();
			solution.setSolutionId(String.valueOf(nxSolutionId));
			solution.setOfferId(bundleCode);
			solution.setSolutionDetails(solutionDetailsList);
			response.setSolution(solution);
		} catch (Exception e) {
			log.error(classNameEx + getnXPEDStatusEx + " Error : While processing get NX PED Status : ", e);
		}
		log.info(classNameEx + getnXPEDStatusEx + " End : ");
		return response;
	}

	public List<SolutionDetails> read(List<Long> nxDesignIds) {
		List<SolutionDetails> solutionDetailsList = nxDesignRepository.findDesignStatusByDesignId(nxDesignIds);
		return solutionDetailsList;
	}
	
}
