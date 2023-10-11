package com.att.sales.nexxus.util;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionUserLockRepository;

@Component
public class NxSolutionUserLockUtil {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxSolutionUserLockUtil.class);
	
	@Autowired
	private NxSolutionUserLockRepository nxSolutionUserLockRepository;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	/*
	 * Update the NX_USER_LOCK_DETAILS and nx_solution_details tables for IS_LOCKED and LOCKED_BY_USER columns. 
	 */

	public void updateSolutionLockStatus(Long nxSolutionId, String userId) {
		try {
			nxSolutionDetailsRepository.updateLockStatusIndBySolutionId(StringConstants.CONSTANT_N, userId,
					nxSolutionId);
			nxSolutionUserLockRepository.updateLockStatusIndBySolutionId(StringConstants.CONSTANT_N,
					new Timestamp(System.currentTimeMillis()), nxSolutionId);
		} catch (Exception ex) {
			log.error("Exception during lock table update{}", ex.getMessage()); 
		}
	}
	
	public void updateSolutionLockStatus(List<Long> nxSolutionIds) {
		try {
			nxSolutionDetailsRepository.updateLockStatusIndBySolutionIdIn(StringConstants.CONSTANT_N, nxSolutionIds);
			nxSolutionUserLockRepository.updateLockStatusIndBySolutionIds(StringConstants.CONSTANT_N,
					new Timestamp(System.currentTimeMillis()), nxSolutionIds);
		} catch (Exception ex) {
			log.error("Exception occured while scheduled unlocking{}", ex.getMessage());
		}
	}
}
