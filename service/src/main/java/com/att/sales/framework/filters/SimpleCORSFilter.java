package com.att.sales.framework.filters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.model.ServiceMetaData;

public class SimpleCORSFilter implements Filter {

	/**
	 * The Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(SimpleCORSFilter.class);

	@Override

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		log.info("> doFilter");

		HttpServletRequest request = (HttpServletRequest) req;
		String headerValue = request.getHeader("Authorization");

		HttpServletResponse response = (HttpServletResponse) resp;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, version, offer,ACTIVITY,*");

		String contentType = request.getHeader("content-type");
		if (contentType == null || !(contentType.startsWith("multipart/form-data") || contentType.startsWith("application/octet-stream"))) {
			if (null != headerValue) {

				chain.doFilter(req, resp);
			}
		} else {
			MultiReadHttpServletRequest requestMultiRead = handleBinraryAttachment(request);
			if (null != headerValue) {

				chain.doFilter(requestMultiRead, resp);
			}
		}

		log.info("< doFilter");
	}

	private MultiReadHttpServletRequest handleBinraryAttachment(HttpServletRequest requestOrg) throws IOException {
		MultiReadHttpServletRequest request = new MultiReadHttpServletRequest(requestOrg);
		Map<String, Object> requestMetaDataHash = new HashMap<>();
		if (requestOrg.getHeader("content-type").startsWith("multipart/form-data")) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				Map<String, List<FileItem>> fileItemMap = upload.parseParameterMap(request);
				for (List<FileItem> fileItems : fileItemMap.values()) {
					for (FileItem fileItem : fileItems) {
						if (!fileItem.isFormField()) {
							String fileName = fileItem.getName();
							InputStream is = fileItem.getInputStream();
							requestMetaDataHash.put(fileName, is);
						}
					}
				}

			} catch (FileUploadException e) {
				log.info("Exception: ", e);
			}
		} else {
			
			requestMetaDataHash.put("inputStream", request.getInputStream());
		}
		ServiceMetaData.add(requestMetaDataHash);
		return request;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("creating {}", SimpleCORSFilter.class.getName());
		
	}

	@Override
	public void destroy() {
		log.info("destroying {}", SimpleCORSFilter.class.getName());
		
	}
}