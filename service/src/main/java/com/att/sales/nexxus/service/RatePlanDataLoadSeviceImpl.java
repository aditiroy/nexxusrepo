package com.att.sales.nexxus.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.prdm.service.PrdmSubscriptionStatusImpl;
import com.att.sales.nexxus.prdm.service.RatePlanAuditDataServiceImpl;

/**
 * The Class RatePlanDataLoadSeviceImpl.
 */
@Service
public class RatePlanDataLoadSeviceImpl extends BaseServiceImpl implements RatePlanDataLoadService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RatePlanDataLoadSeviceImpl.class);
	
	/** The env. */
	@Autowired
	private Environment env;

	/** The file reader helper. */
	@Autowired
	private FileReaderHelper fileReaderHelper;

	/** The prdm subscription status impl. */
	@Autowired
	public PrdmSubscriptionStatusImpl prdmSubscriptionStatusImpl;

	/** The plan audit data service impl. */
	@Autowired
	public RatePlanAuditDataServiceImpl planAuditDataServiceImpl;

	/** The status type. */
	private String statusType = CommonConstants.SUCCESS;
	
	/** The reason code. */
	private String reasonCode = null;
	
	/** The file name. */
	private String fileName = null;
	
	/** The transaction id. */
	private String transactionId = null;

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.RatePlanDataLoadService#putRatePlanDataLoad(com.att.sales.nexxus.model.ProductDataLoadRequest)
	 */
	@Override
	public void putRatePlanDataLoad(ProductDataLoadRequest productDataLoadRequest) throws SalesBusinessException {
		String filePath = env.getProperty("pv.directory.processed");
		logger.info("Entered putProductDataLoad() method");

		try {
			String fileName = null;
			//MultipartBody multipart = productDataLoadRequest.getMultipartBody();

			if (ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME) != null) {
				fileName = ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME).toString();
			}

			reasonCode = validateAndUnzip(fileName);
			setDesignDataLoadData(fileName);

			if (reasonCode != null) {
				logger.info("failed to unzip and load in to tables");
				statusType = CommonConstants.FAILURE;
			}
			BigDecimal fileId = planAuditDataServiceImpl.setAuditData(fileName, "consume", statusType);
			prdmSubscriptionStatusImpl.setPrdmSubscriptionStatus(fileId, reasonCode);

			if (!statusType.equalsIgnoreCase(CommonConstants.FAILURE)) {
				// code for prdmdataload from java achieve tables to working tables
				reasonCode = loadDataFromArchToWrkTablesForPrdm();
				if (reasonCode != null) {
					logger.info("failed to load prdmdataload from archive tables to working to tables");
					statusType = CommonConstants.FAILURE;
				}
				fileId = planAuditDataServiceImpl.setAuditData(fileName, "process", statusType);
				prdmSubscriptionStatusImpl.setPrdmSubscriptionStatus(fileId, reasonCode);
			} else {
				throw new SalesBusinessException(
						"Failed to process prdmdataload from java achieve tables to working tables");
			}

		} catch (Exception e) {
			logger.error("Exception from RatePlanDataLoadSeviceImpl.putRatePlanDataLoad>", e);
			throw new SalesBusinessException(e.getMessage());
		}
	}

	/**
	 * Sets the design data load data.
	 *
	 * @param fileName the new design data load data
	 * @throws SalesBusinessException the sales business exception
	 */
	public void setDesignDataLoadData(String fileName) throws SalesBusinessException {
		if (fileName == null || !fileName.contains("_")) {
			logger.error("Exception while parsing prdmdataload fileName: ");
			throw new SalesBusinessException(MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED);
		}

	}

	/**
	 * Validate and unzip.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	public String validateAndUnzip(String fileName) {
		String filePath = env.getProperty("pv.directory.processed");
		logger.info("Entered validateAndUnzip() method");
		try {
			setDesignDataLoadData(fileName);
			fileReaderHelper.copyToPVAndUnzip(fileName, transactionId);
			List<File> listOfFile = new ArrayList<>();
			
			listOfFile.add(new File(filePath + fileName)); //NOSONAR
		
			 transactionId = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf("."));
			fileReaderHelper.processContents(listOfFile, transactionId);

		} catch (Exception e) {
			logger.error(MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED, e);
			reasonCode = MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED;
		}
		return reasonCode;
	}

	/**
	 * Load data from arch to wrk tables for prdm.
	 *
	 * @return the string
	 */
	public String loadDataFromArchToWrkTablesForPrdm() {
		try {

			dataProcessingForPrdm(transactionId);
		} catch (Exception e) {
			logger.error(MessageConstants.FILE_PROCESSING_DELAYED, e);
			reasonCode = MessageConstants.FILE_PROCESSING_DELAYED;
		}
		return reasonCode;
	}
	
	
	/**
	 * Data processing for prdm.
	 *
	 * @param transactionId the transaction id
	 */
	public void dataProcessingForPrdm(String transactionId)
	{

		Connection connection = null;
		CallableStatement callableStatement=null;

		try {
		/*	Class.forName(env.getProperty("ds.primary.driver-class-name"));
			connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
					env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
		*/
			DriverManagerDataSource datasource = new DriverManagerDataSource();
			datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
			datasource.setUrl(env.getProperty("ds.primary.url"));
			connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			callableStatement = connection.prepareCall("{call RATE_PLAN_INSERT (?)}");
			callableStatement.setString(1, transactionId);
			callableStatement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("failed to load data from Actual tables to Working to tables", e);
			reasonCode = MessageConstants.FILE_PROCESSING_DELAYED;
		}
		finally
		{
			if(null !=connection)
			{
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("SQLException", e);}
			}
			if(null !=callableStatement)
			{
				try {
					callableStatement.close();
				} catch (SQLException e) {
					logger.error("SQLException", e);
				}
			}
		}
		
	}

	
	/**
	 * Load prdm data from working to actual tables.
	 *
	 * @param message the message
	 * @throws SalesBusinessException the sales business exception
	 */
	public void loadPrdmDataFromWorkingToActualTables(String message) throws SalesBusinessException {

		if (message.equalsIgnoreCase(CommonConstants.ROLLBACK)) {

			try {
				fileReaderHelper.dataRollBackPrdm(transactionId);
			} catch (SalesBusinessException e) {
				logger.error("EXception occured during ROLLBACK event", e);
			}
		} else if (message.equalsIgnoreCase(CommonConstants.ACTIVATE)) {

			Connection connection = null;
			CallableStatement callableStatement = null;

			try {
				/*Class.forName(env.getProperty("ds.primary.driver-class-name"));
				connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
						env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));*/
				DriverManagerDataSource datasource = new DriverManagerDataSource();
				datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
				datasource.setUrl(env.getProperty("ds.primary.url"));
				connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));

				callableStatement = connection.prepareCall("{call RATE_PLAN_INSERTUPDATES(?)}");
				callableStatement.setString(1, transactionId);
				callableStatement.executeUpdate();
			
			} catch (Exception e) {
				logger.error("failed to load prdmdataload from Working tables to Actual to tables", e);
				reasonCode = MessageConstants.FILE_PROCESSING_DELAYED;
			}
			finally
			{
				if(null !=connection)
				{
					try {
						connection.close();
					} catch (SQLException e) {
						logger.error("SQLException", e);}
				}
				if(null !=callableStatement)
				{
					try {
						callableStatement.close();
					} catch (SQLException e) {
						logger.error("SQLException", e);
					}
				}
			}
			if (reasonCode != null) {
				statusType = CommonConstants.FAILURE;
			}

			BigDecimal fileId = planAuditDataServiceImpl.setAuditData(fileName, "active", statusType);
			prdmSubscriptionStatusImpl.setPrdmSubscriptionStatus(fileId, reasonCode);
		}
	
	}
}
