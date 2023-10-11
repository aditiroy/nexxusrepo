package com.att.sales.nexxus.p8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.StringConstants;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.ConfigurationParameter;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Configuration;
import com.filenet.api.util.ConfigurationParameters;
import com.filenet.api.util.UserContext;

/**
 * Used to lookup files in AT&T P8 repository.
 *
 * @author ng3775
 */
@Component
public class P8Service {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(P8Service.class);

	/** The p 8 user. */
	@Value("${p8.userName}")
	private String p8User;

	/** The p 8 password. */
	@Value("${p8.passWord}")
	private String p8Password;

	/** The p 8 url. */
	@Value("${p8.url}")
	private String p8Url;

	/** The str obj store. */
	@Value("${p8.objectStore}")
	private String strObjStore;

	/** The p 8 directory. */
	@Value("${p8.directory}")
	private String p8directory;

	/** The p 8 d local path. */
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Value("${azure.http.proxy.host}")
	private String httpProxyHost;
	
	@Value("${azure.http.proxy.port}")
	private String httpProxyPort;

	/**
	 * Looks up P8 repository for INR XMLS based on object store.
	 *
	 * @param fileName the file name
	 */
	public void lookupDocumentInP8(String fileName) {
		// escaping ' in query
		fileName = fileName.replace("'", "''");
		log.info("Looking up for file in p8:{}", fileName);
		try {
			Domain domain = openP8Connection();
			ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domain, strObjStore, null);
			retrieveDocument(objectStore, fileName);
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			log.info("after clear = {}",System.getProperty("https.proxyHost"));
		} catch (IOException|EngineRuntimeException e) {
			log.error("Error in P8 doc search", e);
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			log.info("after clear = {}",System.getProperty("https.proxyHost"));
		}
	}

	/**
	 * Retrieve document.
	 *
	 * @param objectStore the object store
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void retrieveDocument(ObjectStore objectStore, String fileName) throws IOException {
		// To get a folder
		Factory.Folder.getInstance(objectStore, "Folder", p8directory);
		// To search for a document and download it from P8
		folderP8DocSearch(objectStore, fileName);
	}

	/**
	 * Contains main logic to search the repository and retrieve document.
	 *
	 * @param objectStore the object store
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void folderP8DocSearch(ObjectStore objectStore, String fileName) throws IOException {
		SearchSQL sqlObject = new SearchSQL();
		String strWhere = "[Document Title] = '" + fileName + "'";
		sqlObject.setQueryString(
				"SELECT [This], [Id], [Name], [Creator], [ContentElements] FROM [Document] WHERE " + strWhere);
		SearchScope search = new SearchScope(objectStore);
		Integer myPageSize = 500;
		PropertyFilter myFilter = new PropertyFilter();
		myFilter.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.CONTENT_SIZE, null));
		myFilter.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.CONTENT_ELEMENTS, null));
		int myFilterLevel = 1;
		myFilter.setMaxRecursion(myFilterLevel);
		myFilter.addIncludeType(new FilterElement(null, null, null, FilteredPropertyType.ANY, null));
		Boolean continuable = false;
		IndependentObjectSet myObjects = search.fetchObjects(sqlObject, myPageSize, myFilter, continuable);
		Iterator itObjs = myObjects.iterator();
		while (itObjs.hasNext()) {
			Document doc = (Document) itObjs.next();
			log.info("file found with creator: {}", doc.get_Creator());
			ContentElementList docContentList = doc.get_ContentElements();
			Iterator<ContentTransfer> docs = docContentList.iterator();
			if (docs.hasNext()) {
				readFile(docs.next());
			}
		}
	}

	/**
	 * Open streams to read the file.
	 *
	 * @param ct - Input from P8
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void readFile(ContentTransfer ct) throws IOException {
		try (InputStream stream = ct.accessContentStream()) {
			String strName = ct.get_RetrievalName();
			File file = new File(p8dLocalPath + strName);
			try (OutputStream out = new FileOutputStream(file)) {
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = stream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
			}
		} catch (IOException e) {
			log.error("File IO Exception for p8:", e);
			throw e;
		}
	}

	/**
	 * Opens connection to P8.
	 *
	 * @return domain/session object
	 */
	public Domain openP8Connection() {
		Connection ceConnection = null;
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			log.info("before = {}",System.getProperty("https.proxyHost"));
			/*ConfigurationParameters parameters = new ConfigurationParameters();
			Map<String, String> map = new HashMap<String, String>();
			map.put("https.proxyHost", httpProxyHost);
			map.put("https.proxyPort", httpProxyPort);
			parameters.loadMap(map);
			Configuration.init(parameters);*/
			System.setProperty("https.proxyHost", httpProxyHost);
			System.setProperty("https.proxyPort", httpProxyPort);
		}
		ceConnection = Factory.Connection.getConnection(p8Url);
		
		Subject s = UserContext.createSubject(ceConnection, p8User, p8Password, "FileNetP8WSI");
		UserContext uc = UserContext.get();
		uc.pushSubject(s);

		// This Domain object is similar to a SESSION object into P8
		return Factory.Domain.getInstance(ceConnection, null);
	}

	/**
	 * Sets the p 8 user.
	 *
	 * @param p8User the new p 8 user
	 */
	public void setP8User(String p8User) {
		this.p8User = p8User;
	}

	/**
	 * Sets the p 8 password.
	 *
	 * @param p8Password the new p 8 password
	 */
	public void setP8Password(String p8Password) {
		this.p8Password = p8Password;
	}

	/**
	 * Sets the p 8 url.
	 *
	 * @param p8Url the new p 8 url
	 */
	public void setP8Url(String p8Url) {
		this.p8Url = p8Url;
	}

	/**
	 * Sets the str obj store.
	 *
	 * @param strObjStore the new str obj store
	 */
	public void setStrObjStore(String strObjStore) {
		this.strObjStore = strObjStore;
	}

	/**
	 * Sets the p 8 directory.
	 *
	 * @param p8directory the new p 8 directory
	 */
	public void setP8directory(String p8directory) {
		this.p8directory = p8directory;
	}

	/**
	 * Sets the p 8 d local path.
	 *
	 * @param p8dLocalPath the new p 8 d local path
	 */
	public void setP8dLocalPath(String p8dLocalPath) {
		this.p8dLocalPath = p8dLocalPath;
	}

}
