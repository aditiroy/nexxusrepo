package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.FailedBeidDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileAuditModel;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
@ExtendWith(MockitoExtension.class)
public class HybridRepositoryServiceTest {

	@Mock
	private NxOutputFileAuditRepository nxOutputFileAuditRepository;

	@InjectMocks
	HybridRepositoryService hybridRepositoryService;

	@Mock
	private NxTeamRepository nxTeamRepository;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Mock
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private FailedBeidDetailsRepository failedBeidDetailsRepo;
	
	@Mock
	NxRequestGroupRepository nxRequestGroupRepository;


	@Test
	public void testGetNxOutputFileAuditDetails() {
		Long nxOutputFileId = 12345L;
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("activeYn");
		nxSolutionDetail.setCreatedUser("createdUser");
		NxOutputFileAuditModel nxOutputFileAuditModel1 = new NxOutputFileAuditModel();
		nxOutputFileAuditModel1.setFileName("fileName");
		nxOutputFileAuditModel1.setNxOutputFileId(12345L);
		List<NxOutputFileAuditModel> nxOutputFileAuditModel = new ArrayList<>();
		nxOutputFileAuditModel.add(nxOutputFileAuditModel1);

		Mockito.when(nxOutputFileAuditRepository.findByNxOutputFileIdAndNxSolutionDetail(Mockito.anyLong(),
				Mockito.any())).thenReturn(nxOutputFileAuditModel);
		hybridRepositoryService.getNxOutputFileAuditDetails(nxOutputFileId, nxSolutionDetail);
	}

	@Test
	public void testSetNxOutputFileAudit() {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("activeYn");
		nxSolutionDetail.setCreatedUser("createdUser");
		NxOutputFileAuditModel nxOutputFileAuditModel1 = new NxOutputFileAuditModel();
		nxOutputFileAuditModel1.setFileName("fileName");
		Mockito.when(nxOutputFileAuditRepository.save(nxOutputFileAuditModel1)).thenReturn(nxOutputFileAuditModel1);
		hybridRepositoryService.setNxOutputFileAudit(nxOutputFileAuditModel1);
	}

	@Test
	public void testSetNxTeamList() {
		List<NxTeam> nxTeamList = new ArrayList<>();
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		nxTeamList.add(nxTeam);
		Mockito.when(nxTeamRepository.save(nxTeam)).thenReturn(nxTeam);
		hybridRepositoryService.setNxTeamList(nxTeamList);
	}

	@Test
	public void testGetNxTeamList() {
		String attId = "attId";
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("activeYn");
		nxSolutionDetail.setCreatedUser("createdUser");
		List<NxTeam> nxTeamList = new ArrayList<>();
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		nxTeamList.add(nxTeam);
		Mockito.when(nxTeamRepository.findByAttuidAndNxSolutionDetail(Mockito.anyString(), Mockito.any()))
				.thenReturn(nxTeamList);

		hybridRepositoryService.getNxTeamList(attId, nxSolutionDetail);

	}

	@Test
	public void testSetNxSolutionDetailList() {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("activeYn");
		nxSolutionDetail.setCreatedUser("createdUser");
		Mockito.when(nxSolutionDetailsRepository.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		hybridRepositoryService.setNxSolutionDetailList(nxSolutionDetail);
	}

	@Test
	public void testGetNxSolutionDetailList() {
		Long solutionId = 12345L;
		List<NxSolutionDetail> value = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("activeYn");
		nxSolutionDetail.setCreatedUser("createdUser");
		value.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(value.get(0));
		hybridRepositoryService.getNxSolutionDetailList(solutionId);
	}

	@Test
	public void testGetNxOutputFileRepository() {
		Long nxReqId = 12345L;
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<>();
		NxOutputFileModel fileModel = new NxOutputFileModel();
		fileModel.setFallOutData("fallOutData");
		nxOutputFileModels.add(fileModel);
		Mockito.when(nxOutputFileRepository.findByNxReqId(Mockito.anyLong())).thenReturn(nxOutputFileModels);
		hybridRepositoryService.getNxOutputFileRepository(nxReqId);

	}
	
	@Test
	public void testSetNxOutputFileRepository()
	{
		NxOutputFileModel fileModel = new NxOutputFileModel();
		fileModel.setFallOutData("fallOutData");
		Mockito.when(nxOutputFileRepository.save(fileModel)).thenReturn(fileModel);
		hybridRepositoryService.setNxOutputFileRepository(fileModel);
	}

	
	@Test
	public void testSetNxTeam()
	{
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		nxTeam.setIsPryMVG("Y");
		Mockito.when(nxTeamRepository.save(nxTeam)).thenReturn(nxTeam);
		hybridRepositoryService.setNxTeam(nxTeam);
	}
	
	@Test
	public void testGetByRequestId()
	{
		Long nxReqId=12345L;
NxRequestDetails nxRequestDetails = new NxRequestDetails();
Mockito.when(nxRequestDetailsRepository.findByNxReqId(nxReqId)).thenReturn(nxRequestDetails);
		hybridRepositoryService.getByRequestId(nxReqId);
		
	}
	
	@Test
	public void testSaveNxRequestDetails()
	{
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setAcctCriteria("acctCriteria");
		Mockito.when(nxRequestDetailsRepository.save(nxRequestDetails)).thenReturn(nxRequestDetails);
		hybridRepositoryService.saveNxRequestDetails(nxRequestDetails);
	}
	
	@Test
	public void testDeleteNxOutputFileRepository()
	{
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setAcctCriteria("acctCriteria");
		doNothing().when(nxOutputFileRepository).deleteByNxRequestDetails(nxRequestDetails);
		hybridRepositoryService.deleteNxOutputFileRepository(nxRequestDetails);
	}
	
	@Test
	public void testSaveFailedBeidDetails()
	{
		FailedBeidDetails failedBeidDetails = new FailedBeidDetails();
		failedBeidDetails.setFalloutData("falloutData");
		Mockito.when(failedBeidDetailsRepo.save(failedBeidDetails)).thenReturn(failedBeidDetails);
		hybridRepositoryService.saveFailedBeidDetails(failedBeidDetails);
	}
	
	@Test
	public void testFindByGetStatusAction() {
		FalloutDetailsRequest request =new FalloutDetailsRequest();
		request.setCurrentStatus(10L);
		request.setNxReqId(1290L);
		Object[] reqDetailsObjectArray = {new Long(2157),"Submitted"};
		List<Object[]> reqDetailList = new ArrayList<>();
		reqDetailList.add(reqDetailsObjectArray);
		Mockito.when(nxRequestDetailsRepository.findByStatusAndNxReqId(Mockito.anyLong(), 
				Mockito.anyLong())).thenReturn(reqDetailList);
		 List<Object[]> hybridReporesult = hybridRepositoryService.findByGetStatusAction(request);
		 Object[] objResult=hybridReporesult.get(0);
		 assertEquals(objResult[1].toString(),"Submitted");
	}
	
	@Test
	public void testFindByNxRequestGroupId() {
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setActiveYn("Y");
		nxRequestGroup.setDescription("test");
		nxRequestGroup.setGroupId(123L);
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(Mockito.anyLong(),
				Mockito.anyString())).thenReturn(nxRequestGroup);
		NxRequestGroup nxRequestGroupResult=hybridRepositoryService.findByNxRequestGroupId(1L);
		assertEquals(nxRequestGroupResult.getActiveYn(),nxRequestGroup.getActiveYn());
		assertEquals(nxRequestGroupResult.getDescription(),nxRequestGroup.getDescription());
	}

	@Test
	public void testSaveNxRequestGroup() {
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setActiveYn("Y");
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		hybridRepositoryService.saveNxRequestGroup(nxRequestGroup);
	}
	
	@Test
	public void testFindByNxSolutionId() {
		List<NxRequestGroup> nxRequestGroupList = new ArrayList<>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setActiveYn("Y");
		nxRequestGroupList.add(nxRequestGroup);	
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndActiveYn(Mockito.anyLong(),
				Mockito.anyString())).thenReturn(nxRequestGroupList);
		List<NxRequestGroup> nxRequestGroupListResult =hybridRepositoryService.findByNxSolutionId(1L);
		NxRequestGroup nxRequestGroupResult = nxRequestGroupListResult.get(0);
		assertEquals(nxRequestGroup.getActiveYn(), nxRequestGroupResult.getActiveYn());
	}

	@Test
	public void testSaveNxRequestGroups() {
		List<NxRequestGroup> nxRequestGroupList = new ArrayList<>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setActiveYn("Y");
		nxRequestGroupList.add(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		hybridRepositoryService.saveNxRequestGroups(nxRequestGroupList);
	}
	
	@Test
	public void testfindByNxSolutionIdAndStatus() {
		List<NxRequestGroup> nxRequestGroupList = new ArrayList<>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setActiveYn("Y");
		nxRequestGroup.setStatus("Submitted");
		nxRequestGroupList.add(nxRequestGroup);	
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndStatusAndActiveYn(Mockito.anyLong(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(nxRequestGroupList);
		List<NxRequestGroup> nxRequestGroupResultList=hybridRepositoryService.findByNxSolutionIdAndStatus(1L, "Submitted");
		NxRequestGroup nxRequestGroupResult=nxRequestGroupResultList.get(0);
		assertEquals(nxRequestGroup.getActiveYn(),nxRequestGroupResult.getActiveYn());
		assertEquals(nxRequestGroup.getStatus(),nxRequestGroupResult.getStatus());
	}
}
