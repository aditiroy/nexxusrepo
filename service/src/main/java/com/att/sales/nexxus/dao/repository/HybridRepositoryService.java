package com.att.sales.nexxus.dao.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.FailedBeidDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileAuditModel;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;



/**
 * The Class HybridRepositoryService.
 */
@Repository
@Transactional
public class HybridRepositoryService {
	
	/** The nx solution details repository. */
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	/** The nx team repository. */
	@Autowired
	private NxTeamRepository nxTeamRepository;
	
	/** The nx output file audit repository. */
	@Autowired
	private NxOutputFileAuditRepository nxOutputFileAuditRepository;
	
	/** The nx output file repository. */
	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	/** The nx request details repository. */
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	/** The failed beid details repo. */
	@Autowired
	private FailedBeidDetailsRepository failedBeidDetailsRepo;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	/**
	 * Gets the nx output file repository.
	 *
	 * @param nxReqId the nx req id
	 * @return the nx output file repository
	 */
	public List<NxOutputFileModel> getNxOutputFileRepository(Long nxReqId) {
		return nxOutputFileRepository.findByNxReqId(nxReqId);
	}

	/**
	 * Sets the nx output file repository.
	 *
	 * @param nxOutputFileModel the new nx output file repository
	 */
	public void setNxOutputFileRepository(NxOutputFileModel nxOutputFileModel) {
		nxOutputFileRepository.save(nxOutputFileModel);
	}
	
	/**
	 * Gets the nx solution detail list.
	 *
	 * @param solutionId the solution id
	 * @return the nx solution detail list
	 */
	public List<NxSolutionDetail> getNxSolutionDetailList(Long solutionId)
	{
		List<NxSolutionDetail> soln = new ArrayList<NxSolutionDetail>();
		soln.add(nxSolutionDetailsRepository.findByNxSolutionId(solutionId));
		return soln;
	}
	
	/**
	 * Sets the nx solution detail list.
	 *
	 * @param nxSolutionDetail the new nx solution detail list
	 */
	public void setNxSolutionDetailList(NxSolutionDetail nxSolutionDetail) {
		nxSolutionDetailsRepository.save(nxSolutionDetail);
	}
	
	/**
	 * Gets the nx team list.
	 *
	 * @param attId the att id
	 * @param nxSolutionDetail the nx solution detail
	 * @return the nx team list
	 */
	public List<NxTeam> getNxTeamList(String attId, NxSolutionDetail nxSolutionDetail){
		return nxTeamRepository.findByAttuidAndNxSolutionDetail(attId, nxSolutionDetail);
	}
	
	/**
	 * Sets the nx team list.
	 *
	 * @param nxTeamList the new nx team list
	 */
	public void setNxTeamList(List<NxTeam> nxTeamList) {
		nxTeamRepository.saveAll(nxTeamList);
	}
	
	/**
	 * Sets the nx output file audit.
	 *
	 * @param nxOutputFileAuditModel the new nx output file audit
	 */
	public void setNxOutputFileAudit(NxOutputFileAuditModel nxOutputFileAuditModel) {
		nxOutputFileAuditRepository.save(nxOutputFileAuditModel);
	}
	
	/**
	 * Gets the nx output file audit details.
	 *
	 * @param nxOutputFileId the nx output file id
	 * @param nxSolutionDetail the nx solution detail
	 * @return the nx output file audit details
	 */
	public List<NxOutputFileAuditModel> getNxOutputFileAuditDetails(Long nxOutputFileId, NxSolutionDetail nxSolutionDetail){
		return nxOutputFileAuditRepository.findByNxOutputFileIdAndNxSolutionDetail(nxOutputFileId, nxSolutionDetail);
	}
	
	/**
	 * Sets the nx team.
	 *
	 * @param nxTeam the new nx team
	 */
	public void setNxTeam(NxTeam nxTeam) {
		nxTeamRepository.save(nxTeam);
	}
	
	/**
	 * Gets the by request id.
	 *
	 * @param nxReqId the nx req id
	 * @return the by request id
	 */
	public NxRequestDetails getByRequestId(Long nxReqId) {
		return nxRequestDetailsRepository.findByNxReqId(nxReqId);
	}
	
	/**
	 * Save nx request details.
	 *
	 * @param nxRequestDetails the nx request details
	 */
	public void saveNxRequestDetails(NxRequestDetails nxRequestDetails) {
		nxRequestDetailsRepository.save(nxRequestDetails);
	}
	
	/**
	 * Delete nx output file repository.
	 *
	 * @param nxRequestDetails the nx request details
	 */
	public void deleteNxOutputFileRepository(NxRequestDetails nxRequestDetails) {
		nxOutputFileRepository.deleteByNxRequestDetails(nxRequestDetails);
		nxOutputFileRepository.flush();
	}
	
	/**
	 * Save failed beid details.
	 *
	 * @param failedBeidDetails the failed beid details
	 */
	public void saveFailedBeidDetails(FailedBeidDetails failedBeidDetails) {
		failedBeidDetailsRepo.save(failedBeidDetails);
	}
	
	public List<Object[]> findByGetStatusAction(FalloutDetailsRequest request) {
		return nxRequestDetailsRepository.findByStatusAndNxReqId(request.getCurrentStatus(), request.getNxReqId());
	}
	
	public NxRequestGroup findByNxRequestGroupId(Long nxRequestGroupId) {
		return nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(nxRequestGroupId, StringConstants.CONSTANT_Y);
	}
	
	public void saveNxRequestGroup(NxRequestGroup nxRequestGroup) {
		nxRequestGroupRepository.save(nxRequestGroup);
	}
	
	public List<NxRequestGroup> findByNxSolutionId(Long nxSolutionId) {
		return nxRequestGroupRepository.findByNxSolutionIdAndActiveYn(nxSolutionId, StringConstants.CONSTANT_Y);
	}
	
	public void saveNxRequestGroups(List<NxRequestGroup> nxRequestGroup) {
		nxRequestGroupRepository.saveAll(nxRequestGroup);
	}
	
	public List<NxRequestGroup> findByNxSolutionIdAndStatus(Long nxSolutionId, String status) {
		return nxRequestGroupRepository.findByNxSolutionIdAndStatusAndActiveYn(nxSolutionId, status, StringConstants.CONSTANT_Y);
	}
}
