package com.att.sales.nexxus.datarouter.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.datarouter.model.DataRouterRequest;
import com.att.sales.nexxus.datarouter.model.DataRouterResponseBean;
import com.att.sales.nexxus.datarouter.model.FeedFileMetaData;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.Date;

import javax.ws.rs.HttpMethod;

@Service
public class PublishDataRouterServiceImpl extends BaseServiceImpl implements PublishDataRouterService {
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(PublishDataRouterServiceImpl.class);

	@Value("${dr.template.path}")
	private String dataRouterMyPriceUpdatedFilePath;

	@Value("${dr.publish.auth}")
	private String auth;

	@Value("${dr.publish.url}")
	private String feedName;

	@Value("${azure.http.proxy.host}")
	private String proxyServerHost;
	
	@Value("${azure.http.proxy.port}")
	private String proxyServerPort;

	@Value("${azure.proxy.enabled}")
	private String proxyFlag;
	
	boolean status = true;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Override
	public ServiceResponse publishDataRouter(DataRouterRequest request) {
		logger.info("inside publishDataRouter() in PublishDataRouterServiceImpl");
		DataRouterResponseBean response = new DataRouterResponseBean();
		File file = new File(dataRouterMyPriceUpdatedFilePath, FilenameUtils.getName(request.getFileName()));
		FeedFileMetaData feed=new FeedFileMetaData(String.valueOf(file.length()), "1078");
		String output=pushFilesToDMaap(request.getFileName(), null, dataRouterMyPriceUpdatedFilePath, feedName, auth, feed.toString(), 0);
		response.setOutput(output);
		setSuccessResponse(response);
		return response;
	}

	public String pushFilesToDMaap(String fileName, String fileResourcePath, String fileAbsolutePath, String feedName,
			String auth, String metaHeader, int redrctCnt) {
		logger.info("pushFilestoDMaap");
		String output=null;		
		boolean isFailed=false;
		String auditMsg="";
		String auditStatus=null;
		try {
			String sUrl = feedName.concat(fileName);
			logger.info("Publish Url: {}", sUrl);
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			URL url = new URL(sUrl);
			logger.info("fileAbsolutePath: {}", fileAbsolutePath);
			File file = new File(fileAbsolutePath, FilenameUtils.getName(fileName));
			logger.info("File: {}", file);
            Proxy proxy =null;
			if("Y".equalsIgnoreCase(proxyFlag)) {
				proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyServerHost, Integer.parseInt(proxyServerPort)));
				logger.info("PROXY IS SET..!!");
			}
			//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection conn = null; 
			if(null!=proxy){ 
				conn = openConnection(url, proxy); 
			}else { 
				conn = openConnection(url); 
			}
			conn.setInstanceFollowRedirects(false);
			conn.addRequestProperty("Content-Type", "application/octet-stream");
			conn.addRequestProperty("Authorization", auth);
//			conn.addRequestProperty("Expect", "100-Continue");
			conn.addRequestProperty("X-ATT-DR-META", metaHeader);
			logger.info("metaHeader {} ",metaHeader);
			conn.setRequestMethod(HttpMethod.PUT);
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(file.length()); 
			logger.info("file length {}",file.length());
			conn.setRequestProperty("Content-Length", Long.toString(file.length()));
			if (file.isFile()) {
				OutputStream os = null;
				try (FileInputStream fis = new FileInputStream(file)) {
					logger.info("File: {}", file);
					int i = 0;
					long fileLength = file.length();
					int bufSize=(int) (fileLength*2);
					byte[] buf = new byte[bufSize];
					logger.info("File Length: {}", fileLength);

					os = conn.getOutputStream();
					
					if (os != null) {
						while (i < fileLength) {
							int len = buf.length;
							if (fileLength - i < len) {
								len = (int) (fileLength - i);
								logger.info("len: {}", len);
							}
							len = fis.read(buf, 0, len);
							os.write(buf, 0, len);
							i += len;
						}

						os.flush();
						os.close();
					} else {
						auditMsg=auditMsg.concat("conn.getOutputStream() null - for publishing the file "+fileName);
						auditStatus=AuditTrailConstants.FAIL;
//				        printDataRouterAudit(AuditTrailConstants.FAIL, "conn.getOutputStream() null - for publishing the file "+fileName, fileName);
				        logger.info("conn.getOutputStream() null");
					}

				} catch (Exception e) {
					isFailed=true;
					auditMsg=auditMsg.concat("Exception in File Read: %s"+ e.getMessage());
					auditStatus=AuditTrailConstants.FAIL;
//			        printDataRouterAudit(AuditTrailConstants.FAIL, "Exception in File Read: %s"+ e.getMessage(), fileName);
			        logger.error("Exception in File Read: %s", e);
				} finally {
					if (os != null)
						os.close();
				}
				BufferedReader br = null;
				logger.info("Response Message : {}", conn.getResponseMessage());
				logger.info("Response Code :{}", conn.getResponseCode());
				logger.info("Response Headers: {}", conn.getHeaderFields());
				int rc = conn.getResponseCode();
				String lochdr = null;
				String pubid = null;

				if (rc >= 200 && rc < 300) {
					pubid = conn.getHeaderField("X-ATT-DR-PUBLISH-ID");
					logger.info("X-ATT-DR-PUBLISH-ID: {}", pubid);
				} else if (rc >= 300 && rc < 400) {
					lochdr = conn.getHeaderField("Location");
					logger.info("Location Re-Direct: {}", lochdr);
				}
				if (lochdr != null && !lochdr.equalsIgnoreCase(feedName)) {
					auditMsg=auditMsg.concat("Locaiton Re-direct URL : {}"+lochdr.toString());
					auditStatus=AuditTrailConstants.SUCCESS;
//					printDataRouterAudit(AuditTrailConstants.SUCCESS, "Locaiton Re-direct URL : {}"+lochdr.toString(), fileName);
			        logger.info("Locaiton Re-direct URL : {}", lochdr);
					logger.info("Feed Name : {}", feedName);
					String publishUrl = lochdr != null ? lochdr.substring(0, lochdr.lastIndexOf("/")) : null;
					logger.info("publishUrl : {}", publishUrl);
					redrctCnt++;
					if (redrctCnt < 3) {
						pushFilesToDMaap(fileName, null, fileAbsolutePath, publishUrl + "/", auth, metaHeader,
								redrctCnt);
					}

				}
				logger.info("conn.getInputStream(): {}", conn.getInputStream());
				logger.info("conn.getErrorStream(): {}", conn.getErrorStream());

				if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
					br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				} else {
					br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				}
				if ((output = br.readLine()) != null) {
					logger.info("Response: {}", output);

				}
				if (conn.getResponseCode() > 0 && conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
					StringBuilder sb = new StringBuilder();
					logger.info("Http Code : {}", conn.getResponseCode());
					sb.append(" Http Code :").append(conn.getResponseCode());
					logger.info("X-ATT-DR-PUBLISH-ID: {}", pubid);
					sb.append(" X-ATT-DR-PUBLISH-ID: ").append(pubid);
					File sourceFile = new File(file.getAbsolutePath());
					logger.info("Source File Name: {}", sourceFile.getAbsolutePath());
					sb.append(" AbsolutePath: ").append(sourceFile.getAbsolutePath());
					logger.info("File: {} File Exists: {}", sourceFile.getName(), sourceFile.exists());
					sb.append(" File: ").append(sourceFile.getName()).append(" Exists: ").append(sourceFile.exists());
					output = sb.toString();
					auditMsg=auditMsg.concat("X-ATT-DR-PUBLISH-ID: {}".concat(pubid.toString()));
					auditStatus=AuditTrailConstants.SUCCESS;
//			        printDataRouterAudit(AuditTrailConstants.SUCCESS, "X-ATT-DR-PUBLISH-ID: {}".concat(pubid.toString()), fileName);
				}
				conn.disconnect();
			} else {
				auditMsg=auditMsg.concat("File : {} is not present in {}"+ fileName);
				auditStatus=AuditTrailConstants.FAIL;
//		        printDataRouterAudit(AuditTrailConstants.FAIL, "File : {} is not present in {}"+ fileName + fileResourcePath.toString(), fileName);
				output="File : {} is not present in {}"+ fileName + fileResourcePath.toString();
		        logger.info("File : {} is not present in {}", fileName, fileResourcePath);
			}
		} catch (IOException e) {
			status = false;
			if(output == null || !isFailed) {
				auditMsg=auditMsg.concat("Exception in DataRouterPublisherClient %s"+e.getMessage());
				auditStatus=AuditTrailConstants.FAIL;
//		        printDataRouterAudit(AuditTrailConstants.FAIL, "Exception in DataRouterPublisherClient %s"+e.getMessage(), fileName);
			}
			logger.error("Exception in DataRouterPublisherClient %s", e);
			output=e.getMessage();
			return "Exception in DataRouterPublisherClient %s"+e.getMessage();
		}finally {
			printDataRouterAudit(auditStatus, auditMsg, fileName);
		}
        logger.info("EXIT pushFilestoDMaap(): {}", output);
		return output;
	}

	private void printDataRouterAudit(String status, String msg, String fileName) {
		NxDesignAudit nxDesignAudit =  new NxDesignAudit();
		nxDesignAudit.setTransaction(AuditTrailConstants.PUBLISH_DATA_ROUTER);
		nxDesignAudit.setCreatedDate(new Date());
		nxDesignAudit.setModifedDate(new Date());
		nxDesignAudit.setData(msg);
        nxDesignAudit.setStatus(status);
        nxDesignAudit.setNxRefId(null);
        nxDesignAudit.setNxSubRefId(fileName);
        nxDesignAuditRepository.save(nxDesignAudit);
	}

	protected HttpURLConnection openConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}
	
	protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}
	
	
}
