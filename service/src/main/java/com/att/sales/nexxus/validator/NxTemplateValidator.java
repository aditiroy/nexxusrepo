package com.att.sales.nexxus.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.TemplateFileConstants;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;

/**
 * The Class NxTemplateValidator.
 *
 * @author vt393d
 */
public class NxTemplateValidator {
	
	/**
	 * Instantiates a new nx template validator.
	 */
	private NxTemplateValidator() {}

	/**
	 * Validate upload request.
	 *
	 * @param request the request
	 * @throws SalesBusinessException the sales business exception
	 */
	public static void validateUploadRequest(NxTemplateUploadRequest request) throws SalesBusinessException {
		List<String> msg = new ArrayList<>();
		if (request.getInputStream() != null ) {
			if (StringUtils.isNotEmpty(request.getFileName())
					&& request.getFileName().lastIndexOf('.') > 0) {

				String attFilename = request.getFileName();

				String attfileType = attFilename.substring(attFilename.lastIndexOf('.'));
				
				
				if (!(attfileType.equals(TemplateFileConstants.XLSX_EXT)|| 
						attfileType.equals(TemplateFileConstants.XLSM_EXT))) {
					msg.add(TemplateFileConstants.ERROR_FILE_EXTENSION_INVALID);
				} else {
					request.setExtension(attfileType);
				}
				
				validateFileOnFileType(request, msg, attfileType);
				
			} else {
				msg.add(TemplateFileConstants.ERROR_FILE_EXTENSION_INVALID);
			}

		}

		if (request.getInputStream() == null) {
			msg.add(TemplateFileConstants.ERROR_FILE_NOT_EXISTS);
		}
		if (!msg.isEmpty()) {
			throw new SalesBusinessException(msg);
		}

	}

	/**
	 * Validate file on file type.
	 *
	 * @param request the request
	 * @param msg the msg
	 * @param attfileType the attfile type
	 */
	protected static void validateFileOnFileType(NxTemplateUploadRequest request, List<String> msg,
			String attfileType) {
		if(!StringUtils.isNotEmpty(request.getFileType())){
			msg.add(TemplateFileConstants.ERROR_FILE_TYPE_NOT_EXISTS);
		}else {
			if(TemplateFileConstants.VALID_FILE_TYPES.contains(request.getFileType())) {
				if(request.getFileType().equalsIgnoreCase(TemplateFileConstants.OUTPUT_FILE) 
						&& !attfileType.equals(TemplateFileConstants.XLSX_EXT)) {
						msg.add(TemplateFileConstants.ERROR_FILE_INVALID_FOR_OUTPUT_FILE);
				}else if(request.getFileType().equalsIgnoreCase(TemplateFileConstants.CDT_FILE) 
						&& !attfileType.equals(TemplateFileConstants.XLSM_EXT)) {
						msg.add(TemplateFileConstants.ERROR_FILE_INVALID_FOR_CDT_FILE);
				}
			}else {
				msg.add(TemplateFileConstants.ERROR_FILE_TYPE_NOT_EXISTS);
			}
		}
			
			
	}

}
