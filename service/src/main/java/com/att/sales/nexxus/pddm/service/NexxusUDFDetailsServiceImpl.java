package com.att.sales.nexxus.pddm.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.model.ProductDataLoadRequest;

/**
 * The Class NexxusUDFDetailsServiceImpl.
 *
 * @author RudreshWaladaunki
 */
@Service
public class NexxusUDFDetailsServiceImpl extends BaseServiceImpl implements NexxusUDFDetails {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(NexxusUDFDetailsServiceImpl.class);
	
	/** The file reader helper. */
	@Autowired
	public FileReaderHelper fileReaderHelper;

	/** The subscription status service impl. */
	@Autowired
	public SubscriptionStatusServiceImpl subscriptionStatusServiceImpl;

	/** The audit data service impl. */
	@Autowired
	public UDFAuditDataServiceImpl auditDataServiceImpl;

	/** The status type. */
	private String statusType = CommonConstants.SUCCESS;
	
	/** The reason code. */
	private String reasonCode = null;
	
	/** The file name. */
	private String fileName = null;
	
	/** The transaction id. */
	private String transactionId = null;

	/** The env. */
	@Autowired
	private Environment env;

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.pddm.service.NexxusUDFDetails#putNexxusUDFDetails(com.att.sales.nexxus.model.ProductDataLoadRequest)
	 */
	public void putNexxusUDFDetails(ProductDataLoadRequest productDataLoadRequest) throws SalesBusinessException {
		logger.info("Entered putProductDataLoad() method");

		if (ServiceMetaData.getRequestMetaData().get("TransactionId") != null) {
			transactionId = ServiceMetaData.getRequestMetaData().get("TransactionId").toString();
		}

		try {

		//	MultipartBody multipart = productDataLoadRequest.getMultipartBody();

			if (ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME) != null) {
				fileName = ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME).toString();
			}

			reasonCode = validateAndUnzip(fileName);
			if (reasonCode != null) {
				logger.info("failed to unzip and load in to tables");
				statusType = CommonConstants.FAILURE;
			}
			BigDecimal fileId = auditDataServiceImpl.setAuditData(fileName, "consume", statusType);
			subscriptionStatusServiceImpl.setPddmSubscriptionStatus(fileId, reasonCode);

			if (!statusType.equalsIgnoreCase(CommonConstants.FAILURE)) {
				// code for dataload from java achieve tables to working tables
				reasonCode = loadDataFromArchToWrkTables();
				if (reasonCode != null) {
					logger.info("failed to load data from archive tables to working to tables");
					statusType = CommonConstants.FAILURE;
				}
				fileId = auditDataServiceImpl.setAuditData(fileName, "process", statusType);
				subscriptionStatusServiceImpl.setPddmSubscriptionStatus(fileId, reasonCode);
			} else {
				throw new SalesBusinessException(
						"Failed to process dataload from java achieve tables to working tables");
			}

		} catch (Exception e) {
			logger.error("Exception from NexxusServiceImpl.putProductDataLoad>", e);
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
			logger.error("Exception while parsing dataload fileName: ");
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
		logger.info("Entered validateAndUnzip() method");
		try {
			setDesignDataLoadData(fileName);
			fileReaderHelper.copyToPVAndUnzip(fileName,transactionId);
		} catch (Exception e) {
			logger.error(MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED, e);
			reasonCode = MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED;
		}
		return reasonCode;
	}

	/**
	 * Load data from arch to wrk tables.
	 *
	 * @return the string
	 */
	public String loadDataFromArchToWrkTables() {
		try {

			fileReaderHelper.dataProcessing(transactionId);
		} catch (Exception e) {
			logger.error(MessageConstants.FILE_PROCESSING_DELAYED, e);
			reasonCode = MessageConstants.FILE_PROCESSING_DELAYED;
		}
		return reasonCode;
	}

	/**
	 * Load data from working to actual tables.
	 *
	 * @param message the message
	 * @throws SalesBusinessException the sales business exception
	 */
	public void loadDataFromWorkingToActualTables(String message) throws SalesBusinessException {

		if (message.equalsIgnoreCase(CommonConstants.ROLLBACK)) {

			try {
				fileReaderHelper.dataRollBack(transactionId);
			} catch (SalesBusinessException e) {
				logger.error("EXception occured during ROLLBACK event", e);
			}
		} else if (message.equalsIgnoreCase(CommonConstants.ACTIVATE)) {

			Connection connection = null;
			CallableStatement callableStatement = null;

			try {
			/*	Class.forName(env.getProperty("ds.primary.driver-class-name"));
				connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
						env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));*/
				DriverManagerDataSource datasource = new DriverManagerDataSource();
				datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
				datasource.setUrl(env.getProperty("ds.primary.url"));
				connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));

				callableStatement = connection.prepareCall("{call UDF_table_insertupdates(?)}");
				callableStatement.setString(1, transactionId);
				callableStatement.executeUpdate();
				// fileReaderHelper.dataProcessing(transactionId,
				// "loadDataFromWorkingToActualTables");
			} catch (Exception e) {
				logger.error("failed to load data from Working tables to Actual to tables", e);
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

			BigDecimal fileId = auditDataServiceImpl.setAuditData(fileName, "active", statusType);
			subscriptionStatusServiceImpl.setPddmSubscriptionStatus(fileId, reasonCode);
		}
	}

}
