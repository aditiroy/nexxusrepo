package com.att.sales.nexxus.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.TemplateFileConstants;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.template.model.NxTemplateUploadResponse;

/**
 * The Class NxTemplateProcessingService.
 *
 * @author vt393d
 */
@Service
public class NxTemplateProcessingService extends BaseServiceImpl{
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxTemplateProcessingService.class);
	
	
	
	/** The nx outpu template path. */
	@Value("${nx.output.template.path}")
	private String nxOutpuTemplatePath;
	
	/** The cdt file path. */
	@Value("${pv.directory.cdtfile}")
	private String cdtFilePath;
	
	/**
	 * Upload template file.
	 *
	 * @param request the request
	 * @return the nx template upload response
	 * @throws SalesBusinessException the sales business exception
	 */
	public NxTemplateUploadResponse uploadTemplateFile(NxTemplateUploadRequest request) 
			throws SalesBusinessException {
		log.info("Entered uploadTemplateFile File");
		try {
			Path uploadFilepath = null;
			if(request.getFileType().equals(TemplateFileConstants.OUTPUT_FILE)) {
				uploadFilepath=this.getFilePath(nxOutpuTemplatePath);
			}else if(request.getFileType().equals(TemplateFileConstants.CDT_FILE)) {
				uploadFilepath=this.getFilePath(cdtFilePath);
			}
			if(null!=uploadFilepath) {
				this.replaceFile(request.getInputStream(),uploadFilepath);
			}
			
		} catch (IOException e) {
			log.error("IOException in file", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
		
		NxTemplateUploadResponse response=new NxTemplateUploadResponse();
		setSuccessResponse(response);
		return response;
	}
	
	
	/**
	 * Replace file.
	 *
	 * @param uploadedFile the uploaded file
	 * @param uploadFilepath the upload filepath
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void replaceFile(InputStream uploadedFile,Path uploadFilepath) throws IOException {
		log.info("Entered replaceFile File");
		Files.copy(uploadedFile, uploadFilepath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Gets the file path.
	 *
	 * @param filePath the file path
	 * @return the file path
	 * @throws SalesBusinessException the sales business exception
	 */
	public Path getFilePath(String filePath) throws SalesBusinessException {
		log.info("Entered getFilePath File");
		Path validateFilepath = null;
		try {
			File file = new File(filePath); //NOSONAR
			String dir = file.getParent();
			String fileName = file.getName();
			validateFilepath = getFilePathAndCreateFolderIfNotExists(dir,fileName);
			
			log.info("VALIDATE_FOLDER: {}", validateFilepath);

		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException in file", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);

		} catch (Exception e) {
			log.error("uploaded file validation - error in file", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);

		}

		return validateFilepath;
	}
	
	/**
	 * Gets the file path and create folder if not exists.
	 *
	 * @param baseFolder the base folder
	 * @param fileName the file name
	 * @return the file path and create folder if not exists
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public  Path getFilePathAndCreateFolderIfNotExists(String baseFolder,String fileName)
			throws IOException {
		log.info("Entered getFilePathAndCreateFolderIfNotExists File");
		Path folder = getFolderPath(baseFolder);
		if (!folder.toFile().exists()) {
			Files.createDirectories(folder);
		}
		return folder.resolve(fileName);
	}
	
	/**
	 * Gets the folder path.
	 *
	 * @param baseFolder the base folder
	 * @return the folder path
	 */
	private  Path getFolderPath(String baseFolder) {
		log.info("Entered getFolderPath File");
		return  Paths.get(baseFolder); //NOSONAR
	}

}
