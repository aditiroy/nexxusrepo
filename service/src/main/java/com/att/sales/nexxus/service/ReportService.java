package com.att.sales.nexxus.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.admin.model.EdfDownloadRequestDetailsResponse;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.OutputFileConstants;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrPreviewGeneratorV1;
import com.att.sales.nexxus.inr.PreviewDataService;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class ReportService.
 *
 * @author vt393d
 */
@Component
public class ReportService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ReportService.class);
	
	/** The template folder path. */
	@Value("${nx.output.template.path}")
	private String templateFolderPath;
	
	private static String[] columns = {"Main Account Number", "Type","Product","Usage or Non Usage Indicator","Begin Bill Month",
			"Bill Month","CPNI Approver","Customer Name"};
	
	@Autowired
	private NxTemplateProcessingService nxTemplateProcessingService;
	
	/**
	 * Gets the template folder path.
	 *
	 * @return the template folder path
	 */
	public String getTemplateFolderPath() {
		return templateFolderPath;
	}


	/** The template folder path inr. */
	@Value("${nx.inrPreview.template.path}")
	private String templateFolderPathInr;
	
	/**
	 * Gets the template folder path inr.
	 *
	 * @return the template folder path inr
	 */
	public String getTemplateFolderPathInr() {
		return templateFolderPathInr;
	}
	
	/** The nexus output file repository. */
	@Autowired
	private NxOutputFileRepository nexusOutputFileRepository;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The inr factory. */
	@Autowired
	private InrFactory inrFactory;
	
	@Autowired
	private PreviewDataService previewDataService;
	
	/** The em. */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Generate preview inr sheet.
	 *
	 * @param inputReqIds the input req ids
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File generatePreviewInrSheet(List<Long> inputReqIds) throws IOException {
		logger.info("Inside generatePreviewInrSheet method: {}", inputReqIds);
		long startTime = System.currentTimeMillis();
		long endTime;
		Set<Long> requestIds = new HashSet<>(inputReqIds);
		List<NxOutputFileModel> nxOutputFiles = nexusOutputFileRepository.findByMultipleRequestIds(requestIds);
		ArrayNode cdirDataArray = mapper.createArrayNode();
		for (NxOutputFileModel nxOutputFileModel : nxOutputFiles) {
			if (nxOutputFileModel.getCdirData() == null) {
				JsonNode cdirData = previewDataService.generateCdirData(nxOutputFileModel);
				nxOutputFileModel.setCdirData(cdirData.toString());
				nexusOutputFileRepository.save(nxOutputFileModel);
			}
			JsonNode cdirData = previewDataService.updateAuditCheck(nxOutputFileModel);
			cdirDataArray.add(cdirData);
		}
		endTime = System.currentTimeMillis();
		logger.info("time used to get cdir data ready in ms: {}", endTime - startTime);
		startTime = endTime;
		InrPreviewGeneratorV1 inrPreviewGenerator = inrFactory.getInrPreviewGeneratorV1(cdirDataArray,
				inputReqIds.hashCode());
		File file = inrPreviewGenerator.generate();
		endTime = System.currentTimeMillis();
		logger.info("time used to generate cdir excel file in ms: {}", endTime - startTime);
		return file;
	}
	

	/**
	 * Gets the file input stream inr.
	 *
	 * @return the file input stream inr
	 * @throws FileNotFoundException the file not found exception
	 */
	/*PREVIEW INR*/
	protected FileInputStream getFileInputStreamInr() throws FileNotFoundException {
		return new FileInputStream(getTemplateFolderPathInr()); //NOSONAR
	}
	
	
	/*PREVIEW INR*/
		
	/**
	 * Gets the input map preview inr.
	 *
	 * @param outputBean the output bean
	 * @return the input map preview inr
	 */
	protected Map<String, Object> getInputMapPreviewInr(NxOutputBean outputBean){
		logger.info("Inside getInputMapPreviewInr method: {}","");
		Map<String, Object> excelParamsForTranformer=new HashMap<>();
		if(CollectionUtils.isNotEmpty(outputBean.getNxAvpnOutput())) {
			excelParamsForTranformer.put(FmoConstants.PREVIEW_INR_AVPN_TAB,outputBean.getNxAvpnOutput());
		}
				return excelParamsForTranformer;
	}
	
	/**
	 * Gets the workbook.
	 *
	 * @param inputStream the input stream
	 * @return Workbook on the basis of fileType
	 * @throws SalesBusinessException the sales business exception
	 */
	protected Workbook getWorkbook(FileInputStream inputStream) throws SalesBusinessException{
		logger.info("Inside getWorkbook method: {}","");
		Workbook workbook=null;
		try {
			workbook = new XSSFWorkbook(inputStream);
		}
		catch (IOException | EncryptedDocumentException e) {
			logger.error("Exception: ReportingServiceImpl>>getWorkbook()" + e.getMessage(), e);
			throw new SalesBusinessException(e.getMessage());
		}
		return workbook;

	}
	
	
	/**
	 * Generate report.
	 *
	 * @param bean the bean
	 * @return the blob
	 * @throws SalesBusinessException the sales business exception
	 */
	public Blob generateReport(NxOutputBean bean) throws SalesBusinessException {
		FileInputStream fis=null;
		Blob blob=null;
		if(null!=bean) {
			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()){
				fis=this.getFileInputStream();
				Context context = this.getDataMap(bean);
				List<String> sheetList=new ArrayList<>(context.toMap().keySet());
				if(CollectionUtils.isNotEmpty(sheetList)) {
		           PoiTransformer transformer=PoiTransformer.createTransformer(fis, byteStream);
		           this.removeExtraSheets(transformer, sheetList);
		           JxlsHelper.getInstance().processTemplate(context, transformer);
		           blob =this.createBlogObjFromByteArray(byteStream);
				}
				
			} catch ( IOException |SQLException e) {
				logger.info("Exception in creating file {}", e.getMessage(), e);
				throw new SalesBusinessException(MessageConstants.FILE_GENERATION_FAILED);
			} finally {
				if(null!=fis){
					try {
						fis.close();
					} catch (IOException e) {
						logger.info("Exception in closing file inputStreame {}", e.getMessage(), e);
					}
				}
			}
		}
		
		return blob;
		 
	}
	
	/**
	 * Removes the extra sheets.
	 *
	 * @param transformer the transformer
	 * @param sheetNames the sheet names
	 */
	protected void removeExtraSheets(PoiTransformer transformer,List<String> sheetNames) {
		Workbook wb = transformer.getWorkbook();
		for (int i = wb.getNumberOfSheets() - 1; i >= 0; i--) {
			org.apache.poi.ss.usermodel.Sheet templateSheet = wb.getSheetAt(i);
			if (null != templateSheet && StringUtils.isNotEmpty(templateSheet.getSheetName())) {
				this.hideRow(templateSheet, 0);
				String templateSheetName = getTemplateSheetName(templateSheet.getSheetName());
				if (!sheetNames.contains(templateSheetName)) {
					transformer.deleteSheet(templateSheet.getSheetName());
				}
			}
		}
	}
	
	/**
	 * Hide row.
	 *
	 * @param sheet the sheet
	 * @param indexRow the index row
	 */
	private void hideRow(Sheet sheet,int indexRow) {
		Row r = sheet.getRow(indexRow);
		if ( r!=null ) {
		    r.setZeroHeight(true);
		}
	}
	
	/**
	 * Gets the template sheet name.
	 *
	 * @param inputName the input name
	 * @return the template sheet name
	 */
	protected String getTemplateSheetName(String inputName) {
		return inputName.replaceAll("\\s+","_").replaceAll("1-", "");
	}
	
	/**
	 * Gets the data map.
	 *
	 * @param bean the bean
	 * @return the data map
	 */
	protected Context getDataMap(NxOutputBean bean) {
		Context excelParamsForTranformer = new Context();
		if(CollectionUtils.isNotEmpty(bean.getNxAvpnOutput())) {
			excelParamsForTranformer.putVar(OutputFileConstants.AVPN_TAB,bean.getNxAvpnOutput());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxAvpnIntlOutputBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.AVPN_INTERNATIONAL_TAB,bean.getNxAvpnIntlOutputBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxMisDS1AccessBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.MIS_DS1_ACCESS_TAB,bean.getNxMisDS1AccessBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxAvpnDS0DS1AccessBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.AVPN_DS0DS1_ACCESS_TAB,bean.getNxAvpnDS0DS1AccessBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxAvpnDS1FlatRateAccessBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.AVPN_DS1_FLAT_RATE_ACCESS_TAB,bean.getNxAvpnDS1FlatRateAccessBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxDs3AccessBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.DS3_ACCESS_TAB,bean.getNxDs3AccessBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxEthernetAccOutputBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.ETHERNET_ACCESS_TAB,bean.getNxEthernetAccOutputBean());
		}
		if(CollectionUtils.isNotEmpty(bean.getNxAdiMisBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.MIS_TAB,bean.getNxAdiMisBean());
		}
		if (CollectionUtils.isNotEmpty(bean.getNxBvoipOutputBean())) {
			excelParamsForTranformer.putVar(OutputFileConstants.BVOIP_TAB, bean.getNxBvoipOutputBean());
		}
		return excelParamsForTranformer;
	}
	

	/**
	 * Gets the file input stream.
	 *
	 * @return the file input stream
	 * @throws FileNotFoundException the file not found exception
	 */
	protected FileInputStream getFileInputStream() throws FileNotFoundException {
		return new FileInputStream(getTemplateFolderPath()); //NOSONAR
	}
	/**
	 * Creates the Blog obj from byte array.
	 *
	 * @param byteStream the byte stream
	 * @return the blob
	 * @throws SQLException the SQL exception
	 */
	protected Blob createBlogObjFromByteArray(ByteArrayOutputStream byteStream) 
			throws SQLException {
		return new SerialBlob(byteStream.toByteArray());
	}
}