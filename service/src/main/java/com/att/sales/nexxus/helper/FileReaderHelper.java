package com.att.sales.nexxus.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.TableNameConstants;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.util.swagger.APIConstants;
import com.att.sales.util.CpcCSVUtility;
import com.att.sales.util.UnZipUtil;

/**
 * The Class FileReaderHelper.
 *
 * @author DevChouhan
 *   TO read the cpc file via FileReaderHelper
 */
@Component
public class FileReaderHelper implements APIConstants {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(FileReaderHelper.class);

	/** The csv reader util. */
	@Autowired
	private CpcCSVUtility csvReaderUtil;

	/** The unzip util. */
	@Autowired
	private UnZipUtil unzipUtil;

	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * copyToPVAndUnzip method will unzip the file.
	 *
	 * @param fileName the file name
	 * @param transactionId the transaction id
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SalesBusinessException the sales business exception
	 */
	
	public void copyToPVAndUnzip(String fileName, String transactionId) throws IOException, SalesBusinessException {
		

		/**
		 *@comment code is commented 
		 */	
/*
		InputStream multipartFile = (InputStream) ServiceMetaData.getRequestMetaData().get(fileName);
		String filePath = env.getProperty("pv.directory.processed");
		Path path = Paths.get(filePath);
		Path pathWhole = path.resolve(fileName);
		String dirPath = pathWhole.toString();

		String zipFilePath = env.getProperty("pv.directory.processed") + fileName;
		String destinationDirectory = env.getProperty("pv.directory.consumer")
				+ FilenameUtils.removeExtension(fileName);

		log.info("SERVICE LOG IS DIRECTORY EXISTS");

		if (!path.toFile().exists()) {
			try {
				Files.createDirectories(path);
				log.info("SERVICE LOG  DIRECTORIES CREATED SUCCESSFULLY");
			} catch (IOException e) {
				log.error("SERVICE LOG FAIL TO CREATE DIRECTORY {}", e);
			}
		} else {
			File file = new File(dirPath);
			if (file.exists()) {
				boolean isDeleted = file.delete();
				if (isDeleted) {
					Files.createDirectories(path);
				}
			}
		}

		validateTemplateAndUpload(dirPath, multipartFile);*/
		String zipFilePath = env.getProperty("pv.directory.processed") + fileName;
		String destinationDirectory = env.getProperty("pv.directory.consumer")
				+ FilenameUtils.removeExtension(fileName);
		if (FilenameUtils.isExtension(fileName, "zip")) {
			try {
				List<File> listOfFile = unzipUtil.unZip(zipFilePath, destinationDirectory);
				processContents(listOfFile, transactionId);
			} catch (ArchiveException e) {
				log.info("ArchiveException ", e);
			}
		}

	}

	/**
	 * This method is used to validate template before Upload.
	 *
	 * @param dirPath the dir path
	 * @param multipartFile the multipart file
	 * @return request
	 * @throws SalesBusinessException the sales business exception
	 */
	@SuppressWarnings("unused")
	private void validateTemplateAndUpload(String dirPath, InputStream multipartFile) throws SalesBusinessException {
		log.info("Entered validateTemplateAndUpload() method");
		File file = new File(dirPath); //NOSONAR
		try (FileOutputStream os = new FileOutputStream(file)) {
			IOUtils.copy(multipartFile, os);
			multipartFile.close();
		} catch (FileNotFoundException e) {
			log.error("Error message:: ", e);
			throw new SalesBusinessException(MessageConstants.FE4001);
		} catch (IOException e) {
			log.error("SERVICE LOG File Upload Error {}", e);
			throw new SalesBusinessException(MessageConstants.FE4001);
		}
	}

	/**
	 * This method is used to processContents.
	 *
	 * @param archiveContents the archive contents
	 * @param transactionId the transaction id
	 */
	
	public void processContents(List<File> archiveContents, String transactionId) {
		log.info("Entered processContents() method");
		for (File file : archiveContents) {
			try {
				log.info("Entered processContents() method");
				csvReaderUtil.readCSVHeader(file.getCanonicalPath(), transactionId);
			} catch (IOException e) {
				log.info("IOException", e);

			}

		}

	}

	
	/**
	 * This method is used to dataProcessing.
	 *
	 * @param transactionId the transaction id
	 * @throws SalesBusinessException the sales business exception
	 */
	
	public void dataProcessing(String transactionId) throws SalesBusinessException {
		List<String> fromArctables = TableNameConstants.getArchieveTables();
		List<String> toWrkTables = TableNameConstants.getWokingTable();
		dataLaodFromArcToWrk(toWrkTables, fromArctables, transactionId);

	}
	
	
	/**
	 * This method is used to dataLaodFromArcToWrk.
	 *
	 * @param toWrkTables the to wrk tables
	 * @param fromArctables the from arctables
	 * @param transactionId the transaction id
	 * @throws SalesBusinessException the sales business exception
	 * @paramfromArctables 
	 * @paramtransactionId 
	 */
	public void dataLaodFromArcToWrk(List<String> toWrkTables, List<String> fromArctables, String transactionId)
			throws SalesBusinessException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatement2 = null;
		PreparedStatement preparedStatement3 = null;
		PreparedStatement preparedStatement4 = null;

		try {
			/*Class.forName(env.getProperty("ds.primary.driver-class-name"));
			connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
					env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);*/
			DriverManagerDataSource datasource = new DriverManagerDataSource();
			datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
			datasource.setUrl(env.getProperty("ds.primary.url"));
			connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);
			String insert="INSERT INTO ";
			String where=" where TRANSACTIONID =?";
			String query = insert + toWrkTables.get(0)
					+ " SELECT  COMPONENT_ID,COMPONENT_NAME,COMPONENT_DESCRIPTION,RELEASE_CODE_ID,CREATED_DATE,CREATED_USER_ID,MOD_DATE,MOD_USER_ID,ACTIVE,SALES_DATA_YN,TRANSACTION_ID,ACTION,TRANSACTIONID from "
					+ fromArctables.get(0) + where;
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, transactionId);
			preparedStatement.executeUpdate();

			String query1 = insert + toWrkTables.get(1)
					+ " SELECT  OFFER_ID,OFFER_NAME,OFFER_DISPLAY_NAME,OFFER_DESCRIPTION,OFFERNAMEABBR,MOD_DATE,ACTIVE,OFFEREFFDATE,OFFERENDDATE,OFFER_TYPE,OFFER_ORDERABLE_FLAG,OFFER_ATTRIBUTE2,OFFER_ATTRIBUTE1,RELEASE_CODE_ID,OFFER_ID_EXTERNAL,ACTION,TRANSACTIONID from "
					+ fromArctables.get(1) + where;
			preparedStatement2 = connection.prepareStatement(query1);
			preparedStatement2.setString(1, transactionId);
			preparedStatement2.executeUpdate();

			String query2 = insert + toWrkTables.get(2)
					+ " SELECT  PRODCOMP_UDF_ATTR_ID,OFFER_ID,PRODUCT_ID,COMPONENT_ID,UDF_ID,UDF_ATTRIBUTE_ID,UDF_ATTRIBUTE_VALUE,RELEASE_CODE_ID,EFF_DATE,END_DATE,MOD_DATE,ACTIVE,UOM,ATTR_DISPLAY_VALUE,ATTRVAL_ORDER_HAND_OFF,DEFAULT1,SALES_DATA_YN,TRANSACTION_ID,ACTION,TRANSACTIONID from "
					+ fromArctables.get(2) + where;
			preparedStatement3 = connection.prepareStatement(query2);
			preparedStatement3.setString(1, transactionId);
			preparedStatement3.executeUpdate();

			String query3 = insert + toWrkTables.get(3)
					+ " SELECT  UDF_ID,UDF_VALUE,UDF_ABBR,DATA_TYPE,FIELD_LENGTH,FIELD_INDICATOR,MULTIVALUE,EFF_DATE,END_DATE,MOD_DATE,ACTIVE,RELEASE_CODE_ID,SALES_DATA_YN,TRANSACTION_ID,ACTION,TRANSACTIONID from "
					+ fromArctables.get(3) + where;
			preparedStatement4 = connection.prepareStatement(query3);
			preparedStatement4.setString(1, transactionId);
			preparedStatement4.executeUpdate();

			connection.commit();
		} catch (Exception e) {
			log.error("Exception ocuured dataLaodFromArcToWrk ", e);
			try {
				if(null!=connection) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				log.error("SQLException ", e1);
				throw new SalesBusinessException();
			}
			throw new SalesBusinessException();
		} finally {
			closePreparedStatement(connection, preparedStatement, preparedStatement2, preparedStatement3,
					preparedStatement4);
		}

	}

	/**
	 * Close prepared statement.
	 *
	 * @param connection the connection
	 * @param preparedStatement the prepared statement
	 * @param preparedStatement2 the prepared statement 2
	 * @param preparedStatement3 the prepared statement 3
	 * @param preparedStatement4 the prepared statement 4
	 */
	private void closePreparedStatement(Connection connection, PreparedStatement preparedStatement,
			PreparedStatement preparedStatement2, PreparedStatement preparedStatement3,
			PreparedStatement preparedStatement4) {
		if (null != connection) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("Exception occured during closing connection", e);
			}
		}

		if (null != preparedStatement) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("Exception occured during closing preparedStatement ", e);
			}
		}
		if (null != preparedStatement2) {
			try {
				preparedStatement2.close();
			} catch (SQLException e) {
				log.error("Exception occured during closing preparedStatement2 ", e);
			}
		}
		if (null != preparedStatement3) {
			try {
				preparedStatement3.close();
			} catch (SQLException e) {
				log.error("Exception occured during closing preparedStatement3 ", e);
			}
		}
		if (null != preparedStatement4) {
			try {
				preparedStatement4.close();
			} catch (SQLException e) {
				log.error("Exception occured during closing preparedStatement4 ", e);
			}
		}
	}

	/**
	 * This method is used to dataRollBack.
	 *
	 * @param transactionId the transaction id
	 * @throws SalesBusinessException the sales business exception
	 */
	public void dataRollBack(String transactionId) throws SalesBusinessException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			/*Class.forName(env.getProperty("ds.primary.driver-class-name"));
			connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
					env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);*/
			DriverManagerDataSource datasource = new DriverManagerDataSource();
			datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
			datasource.setUrl(env.getProperty("ds.primary.url"));
			connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);
			
			List<String> wrkNames = TableNameConstants.getWokingTable();
			for (String wrkName : wrkNames) {
				String query = "delete from " + wrkName + " where TRANSACTIONID=?";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, transactionId);
				preparedStatement.executeUpdate();

			}

			log.info("DATA process is Successfully");
			// return count;
			connection.commit();
		} catch (Exception e) {
			log.info("Exception occured during dataRollBack{}", e);
			try {
				if(null!=connection) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				log.info("Exception occured while closing connection", e1);
			}
			throw new SalesBusinessException();
		} finally {

			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception", e);
				}
			}
				try {
				if (null != preparedStatement) {
					preparedStatement.close();
				}
				} catch (SQLException e) {
					log.error("Exception", e);
				}

		}

	}
	
	/**
	 * This method is used to dataRollBackPrdm.
	 *
	 * @param transactionId the transaction id
	 * @throws SalesBusinessException the sales business exception
	 */
	
	public void dataRollBackPrdm(String transactionId) throws SalesBusinessException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			/*Class.forName(env.getProperty("ds.primary.driver-class-name"));
			connection = DriverManager.getConnection(env.getProperty("ds.primary.url"),
					env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);*/
			DriverManagerDataSource datasource = new DriverManagerDataSource();
			datasource.setDriverClassName(env.getProperty("ds.primary.driver-class-name"));
			datasource.setUrl(env.getProperty("ds.primary.url"));
			connection = datasource.getConnection(env.getProperty("ds.primary.username"), env.getProperty("ds.primary.password"));
			connection.setAutoCommit(false);
			//	List<String> wrkNames = TableNameConstants.getWokingTable();
		
				String query = "delete from " + TableNameConstants.WA_PRICE_CATALOGUE + " where TRANSACTIONID=?";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, transactionId);
				preparedStatement.executeUpdate();

		

			log.info("DATA process is Successfully");
			// return count;
			connection.commit();
		} catch (Exception e) {
			log.info("Exception occured during dataRollBack{}", e);
			try {
				if(null!=connection) {
					connection.rollback();
				}
			} catch (SQLException e1) {
				log.info("Exception occured while closing connection", e1);
			}
			throw new SalesBusinessException();
		} finally {

			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception", e);
				}
			}
				try {
				if (null != preparedStatement) {
					preparedStatement.close();
				}
				} catch (SQLException e) {
					log.error("Exception", e);
				}

		}

	}

}