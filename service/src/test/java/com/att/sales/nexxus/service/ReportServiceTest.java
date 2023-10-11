package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.NexxusPreviewInrUIModel;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxPreviewInrJsonDao;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrPreviewGeneratorV1;
import com.att.sales.nexxus.inr.PreviewDataService;
import com.att.sales.nexxus.output.entity.NxAvpnIntAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxDsAccessBean;
import com.att.sales.nexxus.output.entity.NxEthernetAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxMisBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class ReportServiceTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

	@Spy
	@InjectMocks
	private ReportService reportService;
	@Mock
	InputStream input;
	@Mock
	private NxPreviewInrJsonDao nxPreviewInrJsonDao;
	
	@Mock
	private NxOutputFileRepository nexusOutputFileRepository;
	
	@Mock
	private PreviewDataService previewDataService;
	
	@Mock
	FileInputStream fileInput;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private InrFactory inrFactory;

	
	private ObjectMapper realMapper = new ObjectMapper();

	
	private String templateFolderPath="src/main/resources/nexxusTemplate/test/testTemplate.xlsx";
	private String templateFolderPathInr="src/main/resources/nexxusTemplate/test";
	
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(reportService, "templateFolderPath", templateFolderPath);
		ReflectionTestUtils.setField(reportService, "templateFolderPathInr", templateFolderPathInr);
	}
	
	@Test
	public void generateReportTest() throws SalesBusinessException {
		NxOutputBean bean=new NxOutputBean();
		NxAvpnOutputBean avpn=new NxAvpnOutputBean();
		avpn.setCity("NY");
		bean.getNxAvpnOutput().add(avpn);
		NxAvpnIntAccessOutputBean avpnInlAccess=new NxAvpnIntAccessOutputBean();
		avpnInlAccess.setCity("CA");
		bean.getNxAvpnIntlOutputBean().add(avpnInlAccess);
		NxEthernetAccessOutputBean etherAccess=new NxEthernetAccessOutputBean();
		etherAccess.setCity("NJ");
		bean.getNxEthernetAccOutputBean().add(etherAccess);
		NxDsAccessBean nxDs3Access=new NxDsAccessBean();
		nxDs3Access.setCity("SA");
		bean.getNxDs3AccessBean().add(nxDs3Access);
		NxDsAccessBean nxMisDS1AccessBean=new NxDsAccessBean();
		nxMisDS1AccessBean.setCity("MA");
		bean.getNxMisDS1AccessBean().add(nxMisDS1AccessBean);
		NxMisBean nxAdiMisBean=new NxMisBean();
		nxAdiMisBean.setCity("KA");
		bean.getNxAdiMisBean().add(nxAdiMisBean);
		NxDsAccessBean nxDsAccessBean=new NxDsAccessBean();
		nxDsAccessBean.setCity("US");
		/*List<NxDsAccessBean> nxAvpnDS0DS1AccessBean=new ArrayList<>();
		nxAvpnDS0DS1AccessBean.add(nxDsAccessBean);*/
		bean.getNxAvpnDS0DS1AccessBean().add(nxDsAccessBean);
		NxDsAccessBean dsAccessBean=new NxDsAccessBean();
		nxDsAccessBean.setCity("Uk");
		bean.getNxAvpnDS1FlatRateAccessBean().add(dsAccessBean);
		doReturn(templateFolderPath).when(reportService).getTemplateFolderPath();
		reportService.generateReport(bean);
	}
	
	@Test
	public void generateReportTestException() throws SalesBusinessException, SQLException {
		NxOutputBean bean=new NxOutputBean();
		NxAvpnOutputBean avpn=new NxAvpnOutputBean();
		avpn.setCity("NY");
		bean.getNxAvpnOutput().add(avpn);
		NxAvpnIntAccessOutputBean avpnInlAccess=new NxAvpnIntAccessOutputBean();
		avpnInlAccess.setCity("CA");
		bean.getNxAvpnIntlOutputBean().add(avpnInlAccess);
		NxEthernetAccessOutputBean etherAccess=new NxEthernetAccessOutputBean();
		etherAccess.setCity("NJ");
		bean.getNxEthernetAccOutputBean().add(etherAccess);
		/*NxDsAccessBean nxDsAccessBean=new NxDsAccessBean();
		nxDsAccessBean.setCity("US");
		List<NxDsAccessBean> nxAvpnDS0DS1AccessBean=new ArrayList<>();
		nxAvpnDS0DS1AccessBean.add(nxDsAccessBean);
		bean.getNxAvpnDS0DS1AccessBean();*/
		doReturn(templateFolderPath).when(reportService).getTemplateFolderPath();
		doThrow(new SQLException()).when(reportService).createBlogObjFromByteArray(any());
		reportService.generateReport(bean);
	}	 
	@SuppressWarnings("unchecked")
	@Test
	public void testGeneratePreviewInrSheet() throws SalesBusinessException, IOException {
		List<Long> inputReqIds=new ArrayList<>();
		Long l1=new Long(2l);
		inputReqIds.add(l1);
		NexxusPreviewInrUIModel model=new NexxusPreviewInrUIModel();
		model.setCustomerName("customerName");
		model.setDunsNumber("dunsNumber");
		model.setIntermediateJson("intermediateJson");
		model.setOptyId("optyId");
		model.setOutputJson("outputJson");
		List<NexxusPreviewInrUIModel> resultantlist=new ArrayList<>();
		resultantlist.add(model);
		Mockito.when(nxPreviewInrJsonDao.getIntermediateJson(Mockito.anyLong())).thenReturn(resultantlist);
		
		List<NxOutputFileModel> nxOutputFiles= new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFiles.add(nxOutputFileModel);
		Mockito.when(nexusOutputFileRepository.findByMultipleRequestIds(Mockito.anySet())).thenReturn(nxOutputFiles);
		String json="{\"test\":\"data\"}";
		JsonNode res= JacksonUtil.toJsonNode(json);
		ArrayNode arrayNode =realMapper.createArrayNode();
		Mockito.when(mapper.createArrayNode()).thenReturn(arrayNode);
		Mockito.when(previewDataService.generateCdirData(Mockito.any())).thenReturn(res);
		Mockito.when(previewDataService.updateAuditCheck(Mockito.any())).thenReturn(res);
		UnmockableWrapper unmockableWrapper = null;
		InrPreviewGeneratorV1 inrPreviewGenerator=new InrPreviewGeneratorV1(arrayNode, "templatePath", "p8dLocalPath",
				unmockableWrapper, 1, mapper);
		Mockito.when(inrFactory.getInrPreviewGeneratorV1(Mockito.any(), Mockito.anyInt())).thenReturn(inrPreviewGenerator);
		try {
		//Mockito.when(reportService.getFileInputStreamInr()).thenReturn(templateFolderPathInr);
		reportService.generatePreviewInrSheet(inputReqIds);
	}catch(Exception e) {
		}
	}

	@Test
	public void getTemplateFolderPathTest(){
		String result=reportService.getTemplateFolderPath();
		assertEquals(templateFolderPath, result);
	}
	
	@Test
	public void getTemplateFolderPathInrTest(){
		String result=reportService.getTemplateFolderPathInr();
		assertEquals(templateFolderPathInr, result);
	}
	
	@Test
	public void testGetInputMapPreviewInr() {
		NxOutputBean outputBean= new NxOutputBean();
		List<NxAvpnOutputBean> nxAvpnOutput = new ArrayList<>();
		NxAvpnOutputBean nxAvpnOutputBean = new NxAvpnOutputBean();
		nxAvpnOutput.add(nxAvpnOutputBean);
		outputBean.setNxAvpnOutput(nxAvpnOutput);
		Map<String, Object> result=reportService.getInputMapPreviewInr(outputBean);
		assertSame(nxAvpnOutput,result.get(FmoConstants.PREVIEW_INR_AVPN_TAB));
	}

}
