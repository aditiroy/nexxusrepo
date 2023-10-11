package com.att.sales.nexxus.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxUserLockDetails;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionUserLockRepository;
import com.att.sales.nexxus.model.SolutionLockRequest;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;

/**
 * Story 888243 Nexxus - As a sales user, I want Nexxus to indicate if someone
 * else is editing the Nexxus solution- BE story
 * 
 * @author nk224q
 *
 */

@Service
public class SolutionLockServiceImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(SolutionLockServiceImpl.class);
	private static final String ERROR_MISSING_ATTUID = "LK0001";
	private static final String ERROR_SOLID_NOT_FOUND = "LK0003";
	private static final String ERROR_SOLID_ALRDY_LOCKED = "LK0004";
	public static final String NONE = "NONE";

	@Autowired
	private NxSolutionUserLockRepository nxSolutionUserLockRepository;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;
	
	@Value("${enable.scheduler}")
	private String enableScheduler;
	
	@Value("${solution.lock.time.in.mins}")
	private String solutionLockTimeInMins;

	public ServiceResponse solutionLockCheck(SolutionLockRequest request) throws SalesBusinessException {
		logger.info("enter checkSolutionLock method");
		validateLockRequest(request);
		ServiceResponse response = new ServiceResponse();

		List<NxUserLockDetails> existingLocks = nxSolutionUserLockRepository
				.findByNxSolutionIdAndIsLocked(Long.valueOf(request.getNxSolutionId()), StringConstants.CONSTANT_Y);

		if (existingLocks.size() > 0 && !request.getIsLocked().equalsIgnoreCase(StringConstants.CONSTANT_N)) {
			throw new SalesBusinessException(ERROR_SOLID_ALRDY_LOCKED);
		}

		// Updated the solution record based on the isLocked flag
		if (request.getIsLocked().equalsIgnoreCase(StringConstants.CONSTANT_Y)) {
			nxSolutionDetailsRepository.updateLockStatusIndBySolutionId(StringConstants.CONSTANT_Y, request.getAttuid(),
					Long.valueOf(request.getNxSolutionId()));

			NxUserLockDetails nxUserLockDetails = new NxUserLockDetails();
			nxUserLockDetails.setNxSolutionId(Long.valueOf(request.getNxSolutionId()));
			nxUserLockDetails.setLockedByUser(request.getAttuid());
			nxUserLockDetails.setIsLocked(StringConstants.CONSTANT_Y);
			nxUserLockDetails.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			nxUserLockDetails.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			nxSolutionUserLockRepository.save(nxUserLockDetails);
		} else if (request.getIsLocked().equalsIgnoreCase(StringConstants.CONSTANT_N)) {
			nxSolutionUserLockUtil.updateSolutionLockStatus(Long.valueOf(request.getNxSolutionId()),
					request.getAttuid());
		}
		logger.info("exit checkSolutionLock method");
		return setSuccessResponse(response);
	}

	protected void validateLockRequest(SolutionLockRequest request) throws SalesBusinessException {
		List<String> messageCodes = new ArrayList<>();
		if (request.getAttuid() == null || request.getAttuid().isEmpty()) {
			messageCodes.add(ERROR_MISSING_ATTUID);
		}
		if (request.getNxSolutionId() == null || request.getNxSolutionId().isEmpty()) {
			messageCodes.add(ERROR_SOLID_NOT_FOUND);
		}
		if (!messageCodes.isEmpty()) {
			throw new SalesBusinessException(messageCodes);
		}
	}

	/**
	 * scheduled job to update isLocked STATUS in NX_SOLUTION_DETAILS table and
	 * NX_USER_LOCK_DETAILS when the solution is locked for more than 30 minutes.
	 */
	@Transactional
	@Scheduled(fixedDelay = 10 * 60 * 1000) // 10 minutes
	public void updateIsLockedStatus() {
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(enableScheduler)) {
			logger.info("updateIsLockedStatus starts {}");
			Date dateThreshold = Date.from(Instant.now().minus(new Integer(solutionLockTimeInMins), ChronoUnit.MINUTES));
			int batchSize = 999;
	
			List<NxUserLockDetails> existingLocks = nxSolutionUserLockRepository.findByModifiedDateAndIsLocked(dateThreshold,
					StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(existingLocks)) {
				List<Long> nxSolutionIds = new ArrayList<Long>();
	
				for (NxUserLockDetails lock : existingLocks) {
					nxSolutionIds.add(lock.getNxSolutionId());
				}
	
				if (nxSolutionIds.size() > 0) {
					if (nxSolutionIds.size() > batchSize) {
						List<Long> nxSolutionIdList = nxSolutionIds.stream().limit(batchSize).collect(Collectors.toList());
						nxSolutionUserLockUtil.updateSolutionLockStatus(nxSolutionIdList);
					} else {
						nxSolutionUserLockUtil.updateSolutionLockStatus(nxSolutionIds);
					}
	
				}
				logger.info("updateIsLockedStatus ends and updated solutions lenght is {}", nxSolutionIds.size());
			}else {
				logger.info("updateIsLockedStatus ends and no locked solutions to update");
			}
		}
	}
}
