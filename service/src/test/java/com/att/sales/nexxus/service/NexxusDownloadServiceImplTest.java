package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockedStatic.Verification;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxOutputFileAuditModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.model.IglooTokenExportResponse;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.model.ZipFileResponse;
import com.att.sales.util.CSVFileWriter;
import com.att.sales.nexxus.util.AuditUtil;

@ExtendWith(MockitoExtension.class)
public class NexxusDownloadServiceImplTest {

	@InjectMocks
	NexxusDownloadServiceImpl test;

	@Mock
	private Environment env;

	@Mock
	HybridRepositoryService hybridRepo;

	@Mock
	private NxOutputFileRepository nexusOutputFileRepository;

	@Mock
	private ReportService reportService;

	@Mock
	private File mockFile;
	
	@Mock
	private NxAccessPricingDataRepository pricingDataRepository;

	@Mock
	private AuditUtil auditUtil;
	
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}

	@Mock
	List<Long> inputReqIds;

	@Mock
	Map<String, Blob> value;

	@Mock
	private List<String> inputFiles;

	@Mock
	private Blob blob;

	@Mock
	NexxusOutputRequest request;

	@Mock
	private ZipFileResponse zipFileResponse;
	
	@Mock
	private BulkUploadEthTokenService bulkUploadEthTokenService;
	
	@Mock
	private Blob file;

	@Test
	public void test() {
		NexxusOutputRequest request1 = new NexxusOutputRequest();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		request1.setRequestIds(inputReqIds);
		test.createFileName("");
		try {
			nxRequestDetails.setStatus(new Long(30));
			Mockito.when(hybridRepo.getByRequestId(inputReqIds.get(0))).thenReturn(nxRequestDetails);
			test.getNexxusOutput(request1);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test.validateAndGetNxSolutionDetails(request);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testvalidateNxRequestDetails() {
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		try {
			nxRequestDetails.setStatus(new Long(100));
			Mockito.when(hybridRepo.getByRequestId(inputReqIds.get(0))).thenReturn(nxRequestDetails);
			test.validateNxRequestDetailsStatus(inputReqIds);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			nxRequestDetails.setStatus(new Long(20));
			Mockito.when(hybridRepo.getByRequestId(inputReqIds.get(0))).thenReturn(nxRequestDetails);
			test.validateNxRequestDetailsStatus(inputReqIds);
			nxRequestDetails.setStatus(new Long(30));
			test.validateNxRequestDetailsStatus(inputReqIds);
			nxRequestDetails.setStatus(new Long(10));
			test.validateNxRequestDetailsStatus(inputReqIds);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			nxRequestDetails.setStatus(new Long(40));
			test.validateNxRequestDetailsStatus(inputReqIds);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			nxRequestDetails.setStatus(new Long(70));
			test.validateNxRequestDetailsStatus(inputReqIds);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetNexxusOutputZipFile() {

		List<Long> inputReqIds = request.getRequestIds();
		List<NxSolutionDetail> value1 = new ArrayList<>();
		NxSolutionDetail detail = new NxSolutionDetail();
		value1.add(detail);

		List<NxOutputFileAuditModel> value3 = new ArrayList<>();
		NxOutputFileAuditModel auditModel = new NxOutputFileAuditModel();
		value3.add(auditModel);
		try {

			request = new NexxusOutputRequest();
			Long nxOutputFileId = 111l;
			request.setNxOutputFileId(nxOutputFileId);
			Long nxSolutionId = 111l;
			request.setNxSolutionId(nxSolutionId);

			NexxusOutputRequest requestNxOutput = new NexxusOutputRequest();
			requestNxOutput.setNxOutputAction("previewInr");
			List<Long> requestIds = new LinkedList<>();
			requestIds.add(new Long(21));
			requestNxOutput.setRequestIds(requestIds);

//			Mockito.when(hybridRepo.getNxOutputFileAuditDetails(nxOutputFileId, detail)).thenReturn(value3);
			Mockito.when(hybridRepo.getNxSolutionDetailList(nxSolutionId)).thenReturn(value1);

			test.getNexxusOutputZipFile(requestNxOutput);
			requestNxOutput.setNxOutputAction("fmoNxOutput");
			request.setRequestIds(requestIds);
			requestNxOutput.setNxSolutionId(nxSolutionId);
			test.getNexxusOutputZipFile(requestNxOutput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test.getOutputBlogObjByReqIds(inputReqIds);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			request = new NexxusOutputRequest();
			Long nxOutputFileId = 111l;
			request.setNxOutputFileId(nxOutputFileId);
			Long nxSolutionId = 111l;
			request.setNxSolutionId(nxSolutionId);
			request.setRequestIds(inputReqIds);
			value.put("fileName", blob);

			test.getNxOutputFileId(request);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			test.getNxOutputFileId(request);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			test.getNexxusOutput(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetZipFileResponse() {
		String cdtfile = env.getProperty("pv.directory.cdtfile");
		String fileName = "abc.txt";
		try {
			test.getZipFileResponse(zipFileResponse, cdtfile, blob, fileName );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testIglooExport() throws SalesBusinessException {
		NexxusOutputRequest requestNxOutput = new NexxusOutputRequest();
		requestNxOutput.setNxOutputAction(StringConstants.IGLOO_EXPORT_ACTION);
		requestNxOutput.setNxSolutionId(101l);
		IglooTokenExportResponse response = new IglooTokenExportResponse();
		response.setFileName("testfile.xls");
		response.setFile(file);
		Mockito.when(bulkUploadEthTokenService.downloadIglooTokenFile(any())).thenReturn(response);
		Mockito.when(env.getProperty(anyString())).thenReturn("src/main/resources/nexxusTemplate/test/");
		try (MockedStatic<CSVFileWriter> mocked = mockStatic(CSVFileWriter.class)) {
			mocked.when((Verification) CSVFileWriter.convertBlobToFile(any(), anyString())).thenReturn(mockFile);
			test.getNexxusOutputZipFile(requestNxOutput);
		}
		
	}

}
